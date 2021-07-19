package br.com.erivelton.pix.chave.servidor

import br.com.erivelton.pix.*
import br.com.erivelton.pix.chave.dto.requisicao.InformacoesChavePix
import br.com.erivelton.pix.chave.servico.BuscaPixServico
import br.com.erivelton.pix.shared.apiexterna.ApiExternaContasItau
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DadosClienteResposta
import br.com.erivelton.pix.chave.servico.RegistraPixServidor
import br.com.erivelton.pix.shared.extensao.toModel
import br.com.erivelton.pix.shared.excecao.ErroDeProcessamentoApiExternaException
import br.com.erivelton.pix.shared.handlers.ErrorAroundHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import java.lang.Exception
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class RegistraChavePixServidor(
    val apiExternaContasItau: ApiExternaContasItau,
    val registraPixService: RegistraPixServidor,
    val buscaPixServico: BuscaPixServico
) : PixGrpcServiceGrpc.PixGrpcServiceImplBase() {

    override fun registrarPix(request: PixRequest, responseObserver: StreamObserver<PixResponse>?) {
        var consultaCliente: HttpResponse<DadosClienteResposta>? = null

        try {
            consultaCliente = apiExternaContasItau.consultaCliente(request.clienteId, request.tipoConta.name)
        } catch (ex: Exception) {
            throw ErroDeProcessamentoApiExternaException("Erro ao trazer os dados cliente do itaú!!")
        }

        val novoPix = request.toModel(consultaCliente.body())
        val chave = registraPixService.salvarPix(novoPix)

        val resposta = PixResponse.newBuilder()
            .setPixID(chave.id!!)
            .setClienteId(chave.idClienteItau())
            .build()

        responseObserver!!.onNext(resposta)
        responseObserver.onCompleted()
    }

    override fun buscaPix(request: DadosPixRequisicao, responseObserver: StreamObserver<DadosPixResposta>?) {
        println("teste1")
        val informacoesPix = request.paraDadosPix()

        val detalhesChavePix = buscaPixServico.busca(informacoesPix)

        val instant = detalhesChavePix.paraInstant()
        val momentoCriacao: Timestamp = Timestamp.newBuilder()
            .setNanos(instant.nano)
            .setSeconds(instant.epochSecond)
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
                .setDadosConta(DadosConta.newBuilder()
                    .setInstituicao(detalhesChavePix.instituicao())
                    .setAgencia(detalhesChavePix.agencia())
                    .setNumero(detalhesChavePix.numero())
                    .setTipoContaBcb(TipoContaBcb.valueOf(detalhesChavePix.tipoConta().name))
                .build())
                .setMomento(momentoCriacao)
                .build()
        )
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