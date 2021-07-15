package br.com.erivelton.pix.shared.apiexterna.dto.resposta

import br.com.erivelton.pix.chave.entidade.Conta
import javax.validation.constraints.NotBlank

data class DadosClienteResponse(
    @field:NotBlank val tipo: String,
    @field:NotBlank val instituicao: InstituicaoResponse,
    @field:NotBlank val agencia: String,
    @field:NotBlank val numero: String,
    @field:NotBlank val titular: TitularResponse
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
}
