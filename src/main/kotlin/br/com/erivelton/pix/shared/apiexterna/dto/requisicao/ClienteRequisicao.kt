package br.com.erivelton.pix.shared.apiexterna.dto.requisicao

import br.com.erivelton.pix.shared.apiexterna.dto.enums.TypePerson

data class ClienteRequisicao(
    val type: TypePerson,
    val name: String,
    val taxIdNumber: String
)