package br.com.erivelton.pix.chave.servidor

import br.com.erivelton.pix.DadosPixRequisicao
import br.com.erivelton.pix.PixRemovidoResposta
import br.com.erivelton.pix.RemovePixGrpcServiceGrpc
import br.com.erivelton.pix.chave.servico.RemovaPixServico
import br.com.erivelton.pix.shared.extensao.paraChaveASerRemovida
import br.com.erivelton.pix.shared.handlers.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class RemovaChavePixServidor(val removaPixServico: RemovaPixServico) :
    RemovePixGrpcServiceGrpc.RemovePixGrpcServiceImplBase() {

    override fun remova(request: DadosPixRequisicao?, responseObserver: StreamObserver<PixRemovidoResposta>?) {
        val clienteASerRemovidoRequisicao = request?.paraChaveASerRemovida()

        clienteASerRemovidoRequisicao?.let { removaPixServico.remova(it) }

        responseObserver?.onNext(PixRemovidoResposta.newBuilder()
            .setMensagem("Chave removida com sucesso")
            .build())

        responseObserver?.onCompleted()
    }
}