package br.com.erivelton.pix.chave.servidor

import br.com.erivelton.pix.*
import br.com.erivelton.pix.chave.dto.requisicao.InformacoesChavePix
import br.com.erivelton.pix.chave.servico.BuscaPixServico
import br.com.erivelton.pix.shared.handlers.ErrorAroundHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.lang.Exception
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class BuscaChavePixServidor(
    val buscaPixServico: BuscaPixServico
) : BuscaGrpcServiceGrpc.BuscaGrpcServiceImplBase() {

    override fun buscaPix(request: DadosPixRequisicao, responseObserver: StreamObserver<DadosPixResposta>?) {
        val informacoesPix = request.paraDadosPix()

        val detalhesChavePix = buscaPixServico.busca(informacoesPix)

        val momentoCriacao: Timestamp = Timestamp.newBuilder()
            .setNanos(detalhesChavePix.paraNano())
            .setSeconds(detalhesChavePix.paraSeconds())
            .build()
        println(detalhesChavePix)
        responseObserver?.onNext(
            DadosPixResposta.newBuilder()
                .setPixId(detalhesChavePix.pixId)
                .setClienteId(detalhesChavePix.clienteId)
                .setTipoChave(TipoChave.valueOf(detalhesChavePix.tipoChave.name))
                .setValorChave(detalhesChavePix.valorChave)
                .setNome(detalhesChavePix.nomeTitular)
                .setCpf(detalhesChavePix.cpfTitular)
                .setDadosConta(
                    DadosConta.newBuilder()
                        .setInstituicao(detalhesChavePix.instituicao())
                        .setAgencia(detalhesChavePix.agencia())
                        .setNumero(detalhesChavePix.numero())
                        .setTipoContaBcb(TipoContaBcb.valueOf(detalhesChavePix.tipoConta().name))
                        .build()
                )
                .setMomento(momentoCriacao)
                .build()
        )
        responseObserver?.onCompleted()
    }

    override fun buscaTodosPixCliente(
        request: InformacaoIdClienteRequisicao,
        responseObserver: StreamObserver<DadosPixGeralResposta>?
    ) {
        val buscaTodos = buscaPixServico.buscaTodos(request.clienteId)

        val map = buscaTodos.map { chave ->
            DadosPixGeralResposta.PixGeralResposta.newBuilder()
                .setClienteId(chave.clienteId)
                .setPixId(chave.id.toString())
                .setTipoChave(TipoChave.valueOf(chave.tipoChave.name))
                .setValorChave(chave.valor)
                .setTipoConta(TipoConta.valueOf(chave.tipoConta.name))
                .setMomento(
                    Timestamp.newBuilder()
                        .setNanos(chave.paraNano())
                        .setSeconds(chave.paraSeconds())
                        .build()
                )
                .build()
        }

        val build = DadosPixGeralResposta.newBuilder()
            .addAllPixGeralResposta(map)
            .build()

        responseObserver?.onNext(build)
        responseObserver?.onCompleted()
    }
}

private fun DadosPixRequisicao.paraDadosPix(): InformacoesChavePix {
    return InformacoesChavePix(
        clienteId = clienteId,
        pixId = pixId.toLong(),
        chavePix = valorChave
    )
}