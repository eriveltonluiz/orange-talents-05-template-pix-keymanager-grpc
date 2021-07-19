package br.com.erivelton.pix.chave.servidor

import br.com.erivelton.pix.PixGrpcServiceGrpc
import br.com.erivelton.pix.PixRequest
import br.com.erivelton.pix.TipoChave
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.entidade.Conta
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.shared.apiexterna.ApiExternaBCB
import br.com.erivelton.pix.shared.apiexterna.ApiExternaContasItau
import br.com.erivelton.pix.shared.apiexterna.dto.enums.AccountType
import br.com.erivelton.pix.shared.apiexterna.dto.enums.TypePerson
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.ClienteRequisicao
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.ContaBancariaRequisicao
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.DadosChavePixRequisicao
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraChavePixServidorTest(
    val repositorio: ChaveRepositorio,
    val grpcClient: PixGrpcServiceGrpc.PixGrpcServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ApiExternaContasItau

    @Inject
    lateinit var apiExternaBCB: ApiExternaBCB

    lateinit var dadosClienteItau: DadosClienteResposta

    lateinit var dadosChavePixRequisicao: DadosChavePixRequisicao

    lateinit var dadosChavePixResposta: DetalhesChavePixResposta

    lateinit var clienteIdPadrao: String

    @BeforeEach
    fun setup() {
        repositorio.deleteAll()
        dadosClienteItau = DadosClienteResposta(
            tipo = TipoConta.CONTA_CORRENTE.name,
            instituicao = InstituicaoResposta(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190"),
            agencia = "0001",
            numero = "123455",
            titular = TitularResposta(
                id = "5260263c-a3c1-4727-ae32-3bdb2538841b",
                nome = "Yuri Matheus",
                cpf = "86135457004"
            )
        )

        dadosChavePixRequisicao = DadosChavePixRequisicao(
            key = "+823713681230",
            keyType = br.com.erivelton.pix.chave.enums.TipoChave.PHONE,
            bankAccount = ContaBancariaRequisicao("60701190", "0001", "123455", AccountType.CACC),
            owner = ClienteRequisicao(TypePerson.NATURAL_PERSON, "Yuri Matheus", "86135457004")
        )

        dadosChavePixResposta = DetalhesChavePixResposta(
            key = "+823713681230",
            keyType = br.com.erivelton.pix.chave.enums.TipoChave.PHONE,
            bankAccount = ContaBancariaResposta("60701190", "0001", "123455", AccountType.CACC),
            owner = ClienteResposta(TypePerson.NATURAL_PERSON, "Yuri Matheus", "86135457004"),
            createdAt = LocalDateTime.now()
        )

        clienteIdPadrao = "5260263c-a3c1-4727-ae32-3bdb2538841b"
    }

    @Test
    internal fun `deve registrar nova chave pix no sistema local e no banco central`() {
        Mockito.`when`(
            itauClient.consultaCliente(
                clienteId = clienteIdPadrao,
                tipo = TipoConta.CONTA_CORRENTE.name
            )
        ).thenReturn(HttpResponse.ok(dadosClienteItau))

        Mockito.`when`(apiExternaBCB.registraPix(dadosChavePixRequisicao))
            .thenReturn(HttpResponse.created(dadosChavePixResposta))

        val resposta = grpcClient.registrarPix(
            PixRequest.newBuilder()
                .setClienteId("5260263c-a3c1-4727-ae32-3bdb2538841b")
                .setValorChave("+823713681230")
                .setTipoChave(TipoChave.PHONE)
                .setTipoConta(br.com.erivelton.pix.TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(resposta) {
            assertEquals(clienteIdPadrao, this.clienteId)
            assertNotNull(pixID)
        }

    }

    @Test
    internal fun `nao deve registrar nova chave pix caso os dados do cliente itau estejam incorretos`() {
        val clienteIdErrado = UUID.randomUUID().toString()
        Mockito.`when`(
            itauClient.consultaCliente(
                clienteId = clienteIdErrado,
                tipo = TipoConta.CONTA_CORRENTE.name
            )
        ).thenReturn(HttpResponse.serverError())

        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.registrarPix(
                PixRequest.newBuilder()
                    .setClienteId(clienteIdErrado)
                    .setValorChave("+823713681230")
                    .setTipoChave(TipoChave.PHONE)
                    .setTipoConta(br.com.erivelton.pix.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        throwGerado.printStackTrace()

        with(throwGerado) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao trazer os dados cliente do itaú!!", status.description)
        }
    }

    @Test
    internal fun `nao deve registrar nova chave pix caso a mesma ja esteja cadastrada`() {
        repositorio.save(
            Chave(
                clienteIdPadrao,
                "+823713681230",
                br.com.erivelton.pix.chave.enums.TipoChave.PHONE,
                TipoConta.CONTA_CORRENTE,
                Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
            )
        )

        Mockito.`when`(itauClient.consultaCliente(clienteIdPadrao, TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(dadosClienteItau))

        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.registrarPix(
                PixRequest.newBuilder()
                    .setClienteId(clienteIdPadrao)
                    .setValorChave("+823713681230")
                    .setTipoChave(TipoChave.PHONE)
                    .setTipoConta(br.com.erivelton.pix.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(throwGerado) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave pix já foi cadastrada para o usuário!!", status.description)
        }

    }

    @Test
    internal fun `nao deve registrar chave pix se houver parametros invalidos`() {
        Mockito.`when`(itauClient.consultaCliente(clienteIdPadrao, TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(dadosClienteItau))

        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.registrarPix(PixRequest.newBuilder().setClienteId(clienteIdPadrao).setTipoConta(br.com.erivelton.pix.TipoConta.CONTA_CORRENTE).build())
        }

        with(throwGerado) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("salvarPix.novoPix: Chave Pix inválida", status.description)
//            MatcherAssert.assertThat(violationsFrom(this), containsInAnyOrder(
//                Pair("clienteId", "must not be blank"),
//                Pair("tipoConta", "must not be null"),
//                Pair("tipoChave", "must not be null")
//            ))
        }
    }

    @MockBean(ApiExternaContasItau::class)
    fun itauClient(): ApiExternaContasItau {
        return Mockito.mock(ApiExternaContasItau::class.java)
    }

    @MockBean(ApiExternaBCB::class)
    fun bcbClient(): ApiExternaBCB{
        return Mockito.mock(ApiExternaBCB::class.java)
    }

    @Factory
    class Clients {

//        @Bean // toda vida que tem @Inject um novo objeto é criada
        @Singleton   // so invoca 1x e compartilha a mesma instância
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixGrpcServiceGrpc.PixGrpcServiceBlockingStub? {
            return PixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}