package br.com.erivelton.pix.chave

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@VerificarChaves
@Introspected
data class NovoPix(
    @field:NotBlank val clienteId: UUID,
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
            valor = valorChave,
            tipoChave = tipoChave,
            tipoConta = tipoConta,
            conta = conta.paraConta()
        )
    }
}
