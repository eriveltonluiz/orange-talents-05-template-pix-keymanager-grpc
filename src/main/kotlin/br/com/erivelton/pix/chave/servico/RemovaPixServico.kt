package br.com.erivelton.pix.chave.servico

import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.shared.excecao.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class RemovaPixServico (@Inject val repositorio: ChaveRepositorio){

    fun remova(@Valid request: ChaveASerRemovidaRequisicao){
        println(request.id)
        val clienteASerRemovido = repositorio
            .findByIdAndClienteId(request.id!!.toLong(), request.clienteId!!)
            .orElseThrow{ChavePixNaoEncontradaException("Chave pix não foi encontrada ou usuário inválido!!")}

        repositorio.delete(clienteASerRemovido)
    }
}