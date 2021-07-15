package br.com.erivelton.pix.shared.apiexterna.dto.requisicao

import br.com.erivelton.pix.chave.entidade.Chave

class DeletaChavePixRequisicao(chave: Chave){
    val key: String = chave.valor
    val participant: String = chave.conta.ispb
}