package br.com.erivelton.pix.chave.validador

import br.com.erivelton.pix.chave.validador.ChavePixValidador
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ChavePixValidador::class])
annotation class VerificarChaves(
    val message: String = "Chave Pix inválida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)
