package br.com.erivelton.pix.shared.extensao

import br.com.erivelton.pix.PixRequest
import br.com.erivelton.pix.chave.dto.requisicao.NovoPix
import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DadosClienteResposta
import javax.validation.Valid

fun PixRequest.toModel(@Valid contaCliente: DadosClienteResposta): NovoPix {
    return NovoPix(
        clienteId,
        valorChave,
        TipoChave.valueOf(tipoChave.name),
        TipoConta.valueOf(tipoConta.name),
        contaCliente
    )
}