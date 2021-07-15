package br.com.erivelton.pix.chave.dto.requisicao

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull

@Introspected
data class ChaveASerRemovidaRequisicao(
    @field:NotNull val id: Long,
    @field:NotNull val clienteId: String
){
}