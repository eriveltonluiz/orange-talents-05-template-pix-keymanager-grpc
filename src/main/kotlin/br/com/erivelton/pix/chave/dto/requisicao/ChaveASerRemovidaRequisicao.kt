package br.com.erivelton.pix.chave.dto.requisicao

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class ChaveASerRemovidaRequisicao(
    @field:NotBlank(message = "ID não pode ser nulo ou vazio") var id: String?,
    @field:NotBlank(message = "clientId não pode ser nulo ou vazio") val clienteId: String?
)