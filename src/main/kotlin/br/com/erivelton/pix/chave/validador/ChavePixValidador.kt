package br.com.erivelton.pix.chave.validador

import br.com.erivelton.pix.chave.dto.requisicao.NovoPix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton

@Singleton
class ChavePixValidador : ConstraintValidator<VerificarChaves, NovoPix> {

    override fun isValid(
        value: NovoPix?,
        annotationMetadata: AnnotationValue<VerificarChaves>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value == null) {
            return false
        }
        return value.retornarValidacaoChaves()
    }

}
