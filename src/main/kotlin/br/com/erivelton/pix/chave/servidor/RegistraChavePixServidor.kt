package br.com.erivelton.pix.chave.servidor

import br.com.erivelton.pix.PixGrpcServiceGrpc
import br.com.erivelton.pix.PixRequest
import br.com.erivelton.pix.PixResponse
import br.com.erivelton.pix.shared.apiexterna.ApiExternaContasItau
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DadosClienteResponse
import br.com.erivelton.pix.chave.servico.RegistraPixService
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.toModel
import br.com.erivelton.pix.shared.excecao.ErroDeProcessamentoApiExternaException
import br.com.erivelton.pix.shared.handlers.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class RegistraChavePixServidor(
    @Inject private val apiExternaContasItau: ApiExternaContasItau,
    @Inject private val registraPixService: RegistraPixService
) : PixGrpcServiceGrpc.PixGrpcServiceImplBase() {

    override fun registrarPix(request: PixRequest?, responseObserver: StreamObserver<PixResponse>?) {
        var consultaCliente: HttpResponse<DadosClienteResponse>? = null

        try {
            consultaCliente = apiExternaContasItau.consultaCliente(request!!.clienteId, request!!.tipoConta.name)
        } catch (ex: HttpClientResponseException) {
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