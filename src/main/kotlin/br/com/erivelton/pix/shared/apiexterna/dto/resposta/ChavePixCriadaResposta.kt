package br.com.erivelton.pix.shared.apiexterna.dto.resposta

import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.shared.apiexterna.dto.enums.TypePerson
import io.micronaut.core.annotation.Introspected

@Introspected
data class ChavePixCriadaResposta(
    val keyType: TipoChave,
    val key: String,
    val bankAccount: ContaBancariaResposta,
    val owner: ClienteResposta,
    val createdAt: String
){
    fun tipoCliente(): TypePerson {
        return owner.type
    }
}