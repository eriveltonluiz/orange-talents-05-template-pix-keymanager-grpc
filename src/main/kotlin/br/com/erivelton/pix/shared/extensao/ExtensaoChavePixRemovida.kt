package br.com.erivelton.pix.shared.extensao

import br.com.erivelton.pix.DadosPixRequisicao
import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import java.util.*

fun DadosPixRequisicao.paraChaveASerRemovida(): ChaveASerRemovidaRequisicao {
    println(pixId.toString().equals(0))
    return ChaveASerRemovidaRequisicao(
        id = if(pixId.toString().equals("0")) null else pixId.toString(),
        clienteId = clienteId
    )
}