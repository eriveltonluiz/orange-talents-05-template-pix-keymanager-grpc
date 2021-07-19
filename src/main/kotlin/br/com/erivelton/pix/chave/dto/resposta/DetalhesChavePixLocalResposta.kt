package br.com.erivelton.pix.chave.dto.resposta

import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.shared.apiexterna.dto.enums.AccountType
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DetalhesChavePixResposta
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class DetalhesChavePixLocalResposta(
    detalhesChavePixResposta: DetalhesChavePixResposta,
    instituicao: String?
){
    val tipoChave: TipoChave = detalhesChavePixResposta.keyType
    val valorChave: String = detalhesChavePixResposta.key
    val nomeTitular: String = detalhesChavePixResposta.owner.name
    val cpfTitular: String = detalhesChavePixResposta.owner.taxIdNumber
    val dadosContaResposta: DadosContaResposta = detalhesChavePixResposta.paraDadosContaResposta(instituicao)
    val momentoCriacao: LocalDateTime = detalhesChavePixResposta.createdAt
    var pixId: String? = null
    var clienteId: String? = null

    fun paraNano(): Int{
        return momentoCriacao.toInstant(ZoneOffset.UTC).nano
    }

    fun paraSeconds(): Long{
        return momentoCriacao.toInstant(ZoneOffset.UTC).epochSecond
    }

    fun instituicao(): String? {
        return dadosContaResposta.instituicao
    }

    fun agencia(): String {
        return dadosContaResposta.agencia
    }

    fun numero(): String {
        return dadosContaResposta.numero
    }

    fun tipoConta(): AccountType {
        return dadosContaResposta.tipoConta
    }

    override fun toString(): String {
        return "DetalhesChavePixLocalResposta(tipoChave=$tipoChave, valorChave='$valorChave', nomeTitular='$nomeTitular', cpfTitular='$cpfTitular', dadosContaResposta=$dadosContaResposta, momentoCriacao=$momentoCriacao, pixId=$pixId, clienteId=$clienteId)"
    }

}