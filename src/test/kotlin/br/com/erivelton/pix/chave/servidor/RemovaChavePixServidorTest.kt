package br.com.erivelton.pix.chave.servidor

import org.junit.jupiter.api.assertThrows
import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.entidade.Conta
import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.removechave.PixRemovidoRequisicao
import br.com.erivelton.pix.removechave.RemovePixGrpcServiceGrpc
import br.com.erivelton.pix.shared.apiexterna.ApiExternaBCB
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.DeletaChavePixRequisicao
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemovaChavePixServidorTest(
    val repositorio: ChaveRepositorio,
    val grpcClient: RemovePixGrpcServiceGrpc.RemovePixGrpcServiceBlockingStub,
//    val grpcClientSalvar: PixGrpcServiceGrpc.PixGrpcServiceBlockingStub
){
    lateinit var clienteIdPadrao: String

    @Inject
    lateinit var apiExternaBCB: ApiExternaBCB

    @BeforeEach
    fun setup(){
        repositorio.deleteAll()
        clienteIdPadrao = "5260263c-a3c1-4727-ae32-3bdb2538841b"
    }

    @Test
    internal fun `deve permitir a exclusao de uma chave pix tanto no BCB quanto local caso os dados sejam validos`() {

        val chaveASerRemovida = Chave(
            clienteIdPadrao,
            "+823713681230",
            TipoChave.PHONE,
            TipoConta.CONTA_CORRENTE,
            Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
        )
        repositorio.save(chaveASerRemovida)

        Mockito.`when`(apiExternaBCB.removePix(DeletaChavePixRequisicao(key = chaveASerRemovida.valor, participant = chaveASerRemovida.conta.ispb), chaveASerRemovida.valor))
            .thenReturn(HttpResponse.ok(ChavePixDeletadaResposta(
                key = "+823713681230",
                participant = "60701190",
                deletedAt = LocalDateTime.now()))
            )

        val chaveRemovida = ChaveASerRemovidaRequisicao("1", clienteIdPadrao)

        val resposta = grpcClient.remova(
            PixRemovidoRequisicao.newBuilder()
                .setPixId(chaveRemovida.id!!.toLong())
                .setClienteId(chaveRemovida.clienteId)
                .build()
        )

        assertEquals("Chave removida com sucesso", resposta.mensagem)

    }
//    @Test
//    internal fun `deve permitir a exclusao de uma chave pix tanto no BCB quanto local caso os dados sejam validos`() {
//        Mockito.`when`(
//            itauClient.consultaCliente(
//                clienteId = clienteIdPadrao,
//                tipo = TipoConta.CONTA_CORRENTE.name
//            )
//        ).thenReturn(HttpResponse.ok(
//            DadosClienteResposta(
//            tipo = TipoConta.CONTA_CORRENTE.name,
//            instituicao = InstituicaoResposta(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190"),
//            agencia = "0001",
//            numero = "123455",
//            titular = TitularResposta(id = "5260263c-a3c1-4727-ae32-3bdb2538841b", nome = "Yuri Matheus", cpf = "86135457004")
//            )
//        ))
//
//        Mockito.`when`(apiExternaBCB.registraPix(
//            DadosChavePixRequisicao(
//            key = "+823713681230",
//            keyType = TipoChave.PHONE,
//            bankAccount = ContaBancariaRequisicao("60701190", "0001", "123455", AccountType.CACC),
//            owner = ClienteRequisicao(TypePerson.NATURAL_PERSON, "Yuri Matheus", "86135457004"))
//        ))
//            .thenReturn(HttpResponse.created(
//                ChavePixCriadaResposta(
//                key = "+823713681230",
//                keyType = TipoChave.PHONE,
//                bankAccount = ContaBancariaResposta("60701190", "0001", "123455", AccountType.CACC),
//                owner = ClienteResposta(TypePerson.NATURAL_PERSON, "Yuri Matheus", "86135457004"),
//                createdAt = LocalDateTime.now())
//            ))
//
//        grpcClient.registrarPix(
//            PixRequest.newBuilder()
//                .setClienteId("5260263c-a3c1-4727-ae32-3bdb2538841b")
//                .setValorChave("+823713681230")
//                .setTipoChave(br.com.erivelton.pix.TipoChave.PHONE)
//                .setTipoConta(br.com.erivelton.pix.TipoConta.CONTA_CORRENTE)
//                .build()
//        )
//
//        val chaveASerRemovida = Chave(
//            clienteIdPadrao,
//            "+823713681230",
//            TipoChave.PHONE,
//            TipoConta.CONTA_CORRENTE,
//            Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
//        )
//        repositorio.save(chaveASerRemovida)
//
//        Mockito.`when`(apiExternaBCB.removePix(DeletaChavePixRequisicao(chave = chaveASerRemovida), chaveASerRemovida.valor))
//            .thenReturn(HttpResponse.ok(ChavePixDeletadaResposta(
//                key = "+823713681230",
//                participant = "60701190",
//                deletedAt = LocalDateTime.now()))
//            )
//
//        val chaveRemovida = ChaveASerRemovidaRequisicao("1", clienteIdPadrao)
//
//        val resposta = grpcClient.remova(
//            DadosPixRequisicao.newBuilder()
//                .setPixId(chaveRemovida.id!!.toLong())
//                .setClienteId(chaveRemovida.clienteId)
//                .build()
//        )
//
//        assertEquals("Chave removida com sucesso", resposta.mensagem)
//    }

    @Test
    internal fun `nao deve permitir a exclusao de uma chave pix caso os dados nao forem preenchidos`() {
        val throwGerado = assertThrows<StatusRuntimeException>{grpcClient.remova(
            PixRemovidoRequisicao.newBuilder()
                .build()
        )}

        with(throwGerado){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("remova.request.clienteId: clientId não pode ser nulo ou vazio, remova.request.id: ID não pode ser nulo ou vazio"
                , status.description)
        }
    }

    @Test
    internal fun `nao deve permitir a exclusao da chave pix caso a mesma não esteja cadastrada`() {
        val chaveRemovida = ChaveASerRemovidaRequisicao("1", clienteIdPadrao)

        val throwGerado = assertThrows<StatusRuntimeException>{ grpcClient.remova(
            PixRemovidoRequisicao.newBuilder()
                .setPixId(chaveRemovida.id!!.toLong())
                .setClienteId(chaveRemovida.clienteId)
                .build()
        )}

        with(throwGerado){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não foi encontrada ou usuário inválido!!", status.description)
        }
    }

    @Test
    internal fun `nao deve permitir a exclusao da chave pix caso não pertenca ao cliente`() {
        val clienteIdNovo = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val chaveRemovida = ChaveASerRemovidaRequisicao("1", clienteIdNovo)

        val throwGerado = assertThrows<StatusRuntimeException>{ grpcClient.remova(
            PixRemovidoRequisicao.newBuilder()
                .setPixId(chaveRemovida.id!!.toLong())
                .setClienteId(chaveRemovida.clienteId)
                .build()
        )}

        with(throwGerado){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não foi encontrada ou usuário inválido!!", status.description)
        }
    }

    @MockBean(ApiExternaBCB::class)
    fun bcbClient(): ApiExternaBCB{
        return Mockito.mock(ApiExternaBCB::class.java)
    }

    @Factory
    class Clients{

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : RemovePixGrpcServiceGrpc.RemovePixGrpcServiceBlockingStub?{
            return RemovePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}