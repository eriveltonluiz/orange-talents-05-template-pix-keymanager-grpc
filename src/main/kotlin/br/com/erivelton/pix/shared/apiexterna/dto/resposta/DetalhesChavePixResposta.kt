package br.com.erivelton.pix.shared.apiexterna.dto.resposta

import br.com.erivelton.pix.chave.dto.resposta.DadosContaResposta
import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.shared.apiexterna.dto.enums.TypePerson
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class DetalhesChavePixResposta(
    val keyType: TipoChave,
    val key: String,
    val bankAccount: ContaBancariaResposta,
    val owner: ClienteResposta,
    val createdAt: LocalDateTime
){
    fun tipoCliente(): TypePerson {
        return owner.type
    }

    fun paraDadosContaResposta(instituicao: String?): DadosContaResposta {
        return DadosContaResposta(
            instituicao = instituicao,
            agencia = bankAccount.branch,
            numero = bankAccount.accountNumber,
            tipoConta = bankAccount.accountType
        )
    }
}