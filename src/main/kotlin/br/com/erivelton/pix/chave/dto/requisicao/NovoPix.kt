package br.com.erivelton.pix.chave.dto.requisicao

import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DadosClienteResposta
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.chave.validador.VerificarChaves
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.ContaBancariaRequisicao
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.DadosChavePixRequisicao
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@VerificarChaves
@Introspected
data class NovoPix(
    @field:NotBlank val clienteId: String,
    @field:Size(max = 77) val valorChave: String,
    @field:NotBlank val tipoChave: TipoChave,
    @field:NotBlank val tipoConta: TipoConta,
    @Valid val conta: DadosClienteResposta,
) {

    fun retornarValidacaoChaves(): Boolean{
        return tipoChave.validaChave(valorChave)
    }

    fun paraEntidadeChave(): Chave {
        return Chave(
            clienteId = clienteId,
            valor = if(tipoChave == TipoChave.RANDOM) UUID.randomUUID().toString() else valorChave,
            tipoChave = tipoChave,
            tipoConta = tipoConta,
            conta = conta.paraConta()
        )
    }

    fun paraBancoCentral(): DadosChavePixRequisicao {
        return DadosChavePixRequisicao(
            keyType = tipoChave,
            key = valorChave,
            bankAccount = conta.paraDadosContaCliente(tipoConta),
            owner = conta.dadosCliente()
        )
    }
}
