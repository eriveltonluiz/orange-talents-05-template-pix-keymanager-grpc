package br.com.erivelton.pix.chave.dto.requisicao

import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DadosClienteResponse
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.chave.validador.VerificarChaves
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@VerificarChaves
@Introspected
data class NovoPix(
    @field:NotBlank val clienteId: String,
    @field:NotBlank @field:Size(max = 77) val valorChave: String,
    @field:NotBlank val tipoChave: TipoChave,
    @field:NotBlank val tipoConta: TipoConta,
    @Valid val conta: DadosClienteResponse,
) {

    fun retornarValidacaoChaves(): Boolean{
        return tipoChave.validaChave(valorChave)
    }

    fun paraEntidadeChave(): Chave {
        return Chave(
            clienteId = clienteId,
            valor = if(tipoChave == TipoChave.CHAVE_ALEATORIA) UUID.randomUUID().toString() else valorChave,
            tipoChave = tipoChave,
            tipoConta = tipoConta,
            conta = conta.paraConta()
        )
    }
}
