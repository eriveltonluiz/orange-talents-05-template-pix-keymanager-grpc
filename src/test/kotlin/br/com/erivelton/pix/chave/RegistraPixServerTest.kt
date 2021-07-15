package br.com.erivelton.pix.chave

import br.com.erivelton.pix.PixGrpcServiceGrpc
import br.com.erivelton.pix.PixRequest
import br.com.erivelton.pix.TipoChave
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.entidade.Conta
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.shared.apiexterna.ApiExternaContasItau
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DadosClienteResponse
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.InstituicaoResponse
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.TitularResponse
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
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraPixServerTest(
    val repositorio: ChaveRepositorio,
    val grpcClient: PixGrpcServiceGrpc.PixGrpcServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ApiExternaContasItau

    lateinit var dadosClienteItau: DadosClienteResponse

    lateinit var clienteIdPadrao: String

    @BeforeEach
    fun setup() {
        repositorio.deleteAll()
        dadosClienteItau = DadosClienteResponse(
            tipo = TipoConta.CONTA_CORRENTE.name,
            instituicao = InstituicaoResponse(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190"),
            agencia = "0001",
            numero = "123455",
            titular = TitularResponse(
                id = "5260263c-a3c1-4727-ae32-3bdb2538841b",
                nome = "Yuri Matheus",
                cpf = "86135457004"
            )
        )
        clienteIdPadrao = "5260263c-a3c1-4727-ae32-3bdb2538841b"
    }

    @Test
    internal fun `deve registrar nova chave pix`() {
        Mockito.`when`(
            itauClient.consultaCliente(
                clienteId = clienteIdPadrao,
                tipo = TipoConta.CONTA_CORRENTE.name
            )
        ).thenReturn(HttpResponse.ok(dadosClienteItau))

        val resposta = grpcClient.registrarPix(
            PixRequest.newBuilder()
                .setClienteId("5260263c-a3c1-4727-ae32-3bdb2538841b")
                .setValorChave("+823713681230")
                .setTipoChave(TipoChave.CELULAR)
                .setTipoConta(br.com.erivelton.pix.TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(resposta) {
            assertEquals(clienteIdPadrao, this.clienteId)
            assertNotNull(pixID)
        }

    }

    @Test
    internal fun `não deve registrar nova chave pix caso os dados do cliente itau estejam incorretos`() {
        val clienteIdErrado = UUID.randomUUID().toString()
        Mockito.`when`(
            itauClient.consultaCliente(
                clienteId = clienteIdErrado,
                tipo = TipoConta.CONTA_CORRENTE.name
            )
        ).thenReturn(HttpResponse.notFound())

        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.registrarPix(
                PixRequest.newBuilder()
                    .setClienteId(clienteIdErrado)
                    .setValorChave("+823713681230")
                    .setTipoChave(TipoChave.CELULAR)
                    .setTipoConta(br.com.erivelton.pix.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(throwGerado) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao trazer os dados cliente do itaú!!", status.description)
        }
    }

    @Test
    internal fun `não deve registrar nova chave pix caso a mesma já esteja cadastrada`() {
        repositorio.save(
            Chave(
                clienteIdPadrao,
                "+823713681230",
                br.com.erivelton.pix.chave.enums.TipoChave.CELULAR,
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
                    .setTipoChave(TipoChave.CELULAR)
                    .setTipoConta(br.com.erivelton.pix.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        throwGerado.printStackTrace()

        with(throwGerado) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave pix já foi cadastrada para o usuário!!", status.description)
        }

    }

    @Test
    internal fun `nao deve registrar chave pix se houver parâmetros inválidos`() {
        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.registrarPix(PixRequest.newBuilder().build())
        }

        with(throwGerado) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
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

    @Factory
    class Clients {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixGrpcServiceGrpc.PixGrpcServiceBlockingStub? {
            return PixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}