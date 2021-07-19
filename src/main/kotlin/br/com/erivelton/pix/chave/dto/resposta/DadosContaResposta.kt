package br.com.erivelton.pix.chave.dto.resposta

import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.shared.apiexterna.dto.enums.AccountType

class DadosContaResposta(
    val instituicao: String?,
    val agencia: String,
    val numero: String,
    val tipoConta: AccountType
)