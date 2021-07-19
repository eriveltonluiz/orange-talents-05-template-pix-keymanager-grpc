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

}