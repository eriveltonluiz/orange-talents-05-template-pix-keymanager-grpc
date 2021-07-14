package br.com.erivelton.pix.chave

import br.com.erivelton.pix.PixGrpcServiceGrpc
import br.com.erivelton.pix.PixRequest
import br.com.erivelton.pix.PixResponse
import br.com.erivelton.pix.shared.handlers.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class RegistraPixServer(@Inject private val apiExternaContasItau: ApiExternaContasItau, @Inject private val registraPixService: RegistraPixService) : PixGrpcServiceGrpc.PixGrpcServiceImplBase(){

    override fun registrarPix(request: PixRequest?, responseObserver: StreamObserver<PixResponse>?) {

        val consultaCliente = apiExternaContasItau.consultaCliente(request!!.clienteId, request!!.tipoConta.name)
        val novoPix = request.toModel(consultaCliente.body())

        val chave = registraPixService.salvarPix(novoPix)

        val response = PixResponse.newBuilder().setPixID(chave.id!!).build()
        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}