package br.com.erivelton.pix.chave.dto.requisicao

import br.com.erivelton.pix.chave.validador.AbordagensPix
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@AbordagensPix
@Introspected
data class InformacoesChavePix(
    val clienteId: String,
    val pixId: Long,
    @field:Size(max = 77, min = 0)val chavePix: String
){
    fun verificarClienteId(): Boolean{
        return clienteId.isNullOrBlank()
    }

    fun verificarChavePix(): Boolean{
        return chavePix.isNullOrBlank()
    }
}
