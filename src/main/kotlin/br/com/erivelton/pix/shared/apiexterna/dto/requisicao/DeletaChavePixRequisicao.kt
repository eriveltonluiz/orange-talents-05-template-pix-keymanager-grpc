package br.com.erivelton.pix.shared.apiexterna.dto.requisicao

import br.com.erivelton.pix.chave.entidade.Chave

data class DeletaChavePixRequisicao(
    val key: String,
    val participant: String,
)