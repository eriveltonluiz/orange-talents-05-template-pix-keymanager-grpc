package br.com.erivelton.pix.shared.handlers

import br.com.erivelton.pix.erro.ChavePixDuplicadaException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorAroundHandler::class)
class ErrorAroundHandlerInterceptor : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        try {
            return context.proceed()
        } catch (ex: Exception) {
            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when (ex) {
                is ChavePixDuplicadaException -> Status.INVALID_ARGUMENT
                    .withCause(ex)
                    .withDescription(ex.message)

                is IllegalArgumentException -> Status.UNKNOWN
                    .withCause(ex)
                    .withDescription(ex.message)

                is ConstraintViolationException -> Status.INVALID_ARGUMENT
                    .withCause(ex)
                    .withDescription(ex.message)

                is HttpClientResponseException -> Status.INVALID_ARGUMENT
                    .withCause(ex)
                    .withDescription("Erro ao conectar com API externa")

                else -> Status.UNKNOWN
                    .withCause(ex)
                    .withDescription("Um erro inseperado ocorreu!")
            }

            responseObserver.onError(status.asRuntimeException())
        }

        return null
    }
}