package br.com.erivelton.pix.shared.handlers

import br.com.erivelton.pix.shared.excecao.ChavePixDuplicadaException
import br.com.erivelton.pix.shared.excecao.ChavePixNaoEncontradaException
import br.com.erivelton.pix.shared.excecao.ErroDeProcessamentoApiExternaException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.lang.IllegalArgumentException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorAroundHandler::class)
class ErrorAroundHandlerInterceptor : MethodInterceptor<Any, Any> {
    private val logger = LoggerFactory.getLogger(ErrorAroundHandlerInterceptor::class.java)
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        try {
            return context.proceed()
        } catch (ex: Exception) {
            logger.info("exceção gerada ${ex.message}")
            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when (ex) {

                is ChavePixDuplicadaException -> Status.ALREADY_EXISTS
                    .withCause(ex)
                    .withDescription(ex.message)

                is ChavePixNaoEncontradaException -> Status.NOT_FOUND
                    .withCause(ex)
                    .withDescription(ex.message)

                is IllegalArgumentException -> Status.UNKNOWN
                    .withCause(ex)
                    .withDescription(ex.message)

                is ConstraintViolationException -> Status.INVALID_ARGUMENT
                    .withCause(ex)
                    .withDescription(ex.message)

                is ErroDeProcessamentoApiExternaException -> Status.FAILED_PRECONDITION
                    .withCause(ex)
                    .withDescription(ex.message)

                else -> Status.UNKNOWN
                    .withCause(ex)
                    .withDescription("Um erro inseperado ocorreu!")
            }

            responseObserver.onError(status.asRuntimeException())
        }

        return null
    }
}