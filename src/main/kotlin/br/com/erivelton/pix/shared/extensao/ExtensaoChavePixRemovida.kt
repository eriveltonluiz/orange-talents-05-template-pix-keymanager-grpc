package br.com.erivelton.pix.shared.extensao

import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import br.com.erivelton.pix.removechave.DadosPixRequisicao
import java.util.*

fun DadosPixRequisicao.paraChaveASerRemovida(): ChaveASerRemovidaRequisicao {
    println(pixId.toString().equals(0))
    return ChaveASerRemovidaRequisicao(
        id = if(pixId.toString().equals("0")) null else pixId.toString(),
        clienteId = clienteId
    )
}