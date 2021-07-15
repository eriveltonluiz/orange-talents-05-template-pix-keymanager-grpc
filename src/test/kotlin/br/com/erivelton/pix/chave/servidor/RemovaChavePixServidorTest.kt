package br.com.erivelton.pix.chave.servidor

import org.junit.jupiter.api.assertThrows
import br.com.erivelton.pix.DadosPixRequisicao
import br.com.erivelton.pix.RemovePixGrpcServiceGrpc
import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.entidade.Conta
import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemovaChavePixServidorTest(
    val repositorio: ChaveRepositorio,
    val grpcClient: RemovePixGrpcServiceGrpc.RemovePixGrpcServiceBlockingStub
){
    lateinit var clienteIdPadrao: String

    @BeforeEach
    fun setup(){
        repositorio.deleteAll()
        clienteIdPadrao = "5260263c-a3c1-4727-ae32-3bdb2538841b"
    }

    @Test
    internal fun `deve permitir a exclusao de uma chave pix caso os dados sejam validos`() {
        repositorio.save(
            Chave(
                clienteIdPadrao,
                "+823713681230",
                TipoChave.CELULAR,
                TipoConta.CONTA_CORRENTE,
                Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
            )
        )
        val chaveRemovida = ChaveASerRemovidaRequisicao("1", clienteIdPadrao)

        val resposta = grpcClient.remova(
            DadosPixRequisicao.newBuilder()
                .setPixId(chaveRemovida.id!!.toLong())
                .setClienteId(chaveRemovida.clienteId)
                .build()
        )

        assertEquals("Chave removida com sucesso", resposta.mensagem)
    }

    @Test
    internal fun `nao deve permitir a exclusao de uma chave pix caso os dados nao forem preenchidos`() {
        val throwGerado = assertThrows<StatusRuntimeException>{grpcClient.remova(
            DadosPixRequisicao.newBuilder()
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
            DadosPixRequisicao.newBuilder()
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
            DadosPixRequisicao.newBuilder()
                .setPixId(chaveRemovida.id!!.toLong())
                .setClienteId(chaveRemovida.clienteId)
                .build()
        )}

        with(throwGerado){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não foi encontrada ou usuário inválido!!", status.description)
        }
    }

    @Factory
    class Clients{

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME)channel: ManagedChannel) : RemovePixGrpcServiceGrpc.RemovePixGrpcServiceBlockingStub{
            return RemovePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}