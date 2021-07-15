package br.com.erivelton.pix.shared.apiexterna.dto.resposta

import br.com.erivelton.pix.shared.apiexterna.dto.enums.TypePerson
import io.micronaut.core.annotation.Introspected

@Introspected
data class ClienteResposta(
    val type: TypePerson,
    val name: String,
    val taxIdNumber: String
)
