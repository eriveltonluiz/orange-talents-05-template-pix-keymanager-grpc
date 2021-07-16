package br.com.erivelton.pix.shared.apiexterna.dto.resposta

import br.com.erivelton.pix.chave.entidade.Conta
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.shared.apiexterna.dto.enums.AccountType
import br.com.erivelton.pix.shared.apiexterna.dto.enums.TypePerson
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.ClienteRequisicao
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.ContaBancariaRequisicao
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class DadosClienteResposta(
    @field:NotBlank val tipo: String,
    @field:NotBlank val instituicao: InstituicaoResposta,
    @field:NotBlank val agencia: String,
    @field:NotBlank val numero: String,
    @field:NotBlank val titular: TitularResposta
){
    fun paraConta(): Conta {
        return Conta(
            instituicao = instituicao.nome,
            ispb = instituicao.ispb,
            agencia = agencia,
            numero = numero,
            nomeTitular = titular.nome,
            cpfTitular = titular.cpf
        )
    }

    fun paraDadosContaCliente(tipoConta: TipoConta): ContaBancariaRequisicao {
        return ContaBancariaRequisicao(
            participant = instituicao.ispb,
            branch = agencia,
            accountNumber = numero,
            accountType = if(tipoConta.equals(TipoConta.CONTA_CORRENTE)) AccountType.CACC else AccountType.SVGS
        )
    }

    fun dadosCliente(): ClienteRequisicao {
        return ClienteRequisicao(
            type = TypePerson.NATURAL_PERSON,
            name = titular.nome,
            taxIdNumber = titular.cpf
        )
    }
}
