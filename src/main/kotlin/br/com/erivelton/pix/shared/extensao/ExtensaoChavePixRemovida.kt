package br.com.erivelton.pix.shared.extensao

import br.com.erivelton.pix.DadosPixRequisicao
import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import java.util.*

fun DadosPixRequisicao.paraChaveASerRemovida(): ChaveASerRemovidaRequisicao? {
    return ChaveASerRemovidaRequisicao(
        id = pixId,
        clienteId = clienteId
    )
}