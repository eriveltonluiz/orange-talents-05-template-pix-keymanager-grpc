package br.com.erivelton.pix.chave.servico

import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.shared.excecao.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class RemovaPixServico (val repositorio: ChaveRepositorio){

    fun remova(@Valid request: ChaveASerRemovidaRequisicao){
        val clienteASerRemovido = repositorio
            .findByIdAndClienteId(request.id, request.clienteId)
            .orElseThrow{ChavePixNaoEncontradaException("ID do pix não foi encontrado!!")}

        repositorio.delete(clienteASerRemovido)
    }
}