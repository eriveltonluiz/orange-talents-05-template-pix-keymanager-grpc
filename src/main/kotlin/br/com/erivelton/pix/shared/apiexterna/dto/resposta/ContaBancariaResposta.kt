package br.com.erivelton.pix.shared.apiexterna.dto.resposta

import br.com.erivelton.pix.shared.apiexterna.dto.enums.AccountType
import io.micronaut.core.annotation.Introspected

@Introspected
data class ContaBancariaResposta(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)
