package br.com.erivelton.pix.chave

import br.com.erivelton.pix.PixRequest
import java.util.*

fun PixRequest.toModel(contaCliente: DadosClienteResponse): NovoPix {
    return NovoPix(
        UUID.fromString(clienteId),
        valorChave,
        TipoChave.valueOf(tipoChave.name),
        TipoConta.valueOf(tipoConta.name),
        contaCliente
    )
}