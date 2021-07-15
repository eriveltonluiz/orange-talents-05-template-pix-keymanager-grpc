package br.com.erivelton.pix.shared.apiexterna.dto.requisicao

import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.shared.apiexterna.dto.enums.AccountType

data class ContaBancariaRequisicao(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)
