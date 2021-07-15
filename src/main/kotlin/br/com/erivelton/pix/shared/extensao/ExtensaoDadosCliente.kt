package br.com.erivelton.pix.shared.apiexterna.dto.resposta

import br.com.erivelton.pix.PixRequest
import br.com.erivelton.pix.chave.dto.requisicao.NovoPix
import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta

fun PixRequest.toModel(contaCliente: DadosClienteResposta): NovoPix {
    return NovoPix(
        clienteId,
        valorChave,
        TipoChave.valueOf(tipoChave.name),
        TipoConta.valueOf(tipoConta.name),
        contaCliente
    )
}