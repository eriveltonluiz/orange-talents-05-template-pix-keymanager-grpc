package br.com.erivelton.pix.shared.extensao

import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import br.com.erivelton.pix.removechave.PixRemovidoRequisicao
import java.util.*

fun PixRemovidoRequisicao.paraChaveASerRemovida(): ChaveASerRemovidaRequisicao {
    println(pixId.toString().equals(0))
    return ChaveASerRemovidaRequisicao(
        id = if(pixId.toString().equals("0")) null else pixId.toString(),
        clienteId = clienteId
    )
}