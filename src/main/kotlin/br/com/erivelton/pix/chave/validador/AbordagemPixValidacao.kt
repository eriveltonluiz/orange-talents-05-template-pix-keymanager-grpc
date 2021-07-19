package br.com.erivelton.pix.chave.validador

import br.com.erivelton.pix.chave.dto.requisicao.InformacoesChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton

@Singleton
class AbordagemPixValidacao : ConstraintValidator<AbordagensPix, InformacoesChavePix> {
    override fun isValid(
        value: InformacoesChavePix,
        annotationMetadata: AnnotationValue<AbordagensPix>,
        context: ConstraintValidatorContext
    ): Boolean {
        println(value)
        if (value.verificarClienteId() && value.pixId == 0L && value.verificarChavePix())
            return false
        if (!value.verificarChavePix() && (!value.verificarClienteId() || value.pixId != 0L))
            return false
        if (value.verificarClienteId() && value.pixId != 0L)
            return false
        if (!value.verificarClienteId() && value.pixId == 0L)
            return false
        return true
    }

}
