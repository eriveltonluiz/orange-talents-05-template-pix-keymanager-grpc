package br.com.erivelton.pix.chave.servidor

import br.com.erivelton.pix.DadosPixRequisicao
import br.com.erivelton.pix.DadosPixResposta
import br.com.erivelton.pix.PixGrpcServiceGrpc
import br.com.erivelton.pix.TipoChave
import br.com.erivelton.pix.chave.dto.requisicao.InformacoesChavePix
import br.com.erivelton.pix.chave.servico.BuscaPixServico
import br.com.erivelton.pix.shared.handlers.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class BuscaChavePixServidor(val buscaPixServico: BuscaPixServico) : PixGrpcServiceGrpc.PixGrpcServiceImplBase() {

    override fun buscaPix(request: DadosPixRequisicao, responseObserver: StreamObserver<DadosPixResposta>?) {
        val informacoesPix = request.paraDadosPix()

        buscaPixServico.busca(informacoesPix)

        responseObserver?.onNext(DadosPixResposta.newBuilder().setClienteId("1").setTipoChave(TipoChave.PHONE).setValorChave("dsaf").build())
        responseObserver?.onCompleted()
    }
}

private fun DadosPixRequisicao.paraDadosPix() : InformacoesChavePix {
    return InformacoesChavePix(
        clienteId = clienteId,
        pixId = pixId.toLong(),
        chavePix = valorChave
    )
}
