package br.com.erivelton.pix.shared.apiexterna.dto.requisicao

import br.com.erivelton.pix.chave.enums.TipoChave

data class DadosChavePixRequisicao(
    val keyType: TipoChave,
    val key: String,
    val bankAccount: ContaBancariaRequisicao,
    val owner: ClienteRequisicao
)