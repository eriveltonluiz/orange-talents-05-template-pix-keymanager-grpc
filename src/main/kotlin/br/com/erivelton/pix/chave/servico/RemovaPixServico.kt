package br.com.erivelton.pix.chave.servico

import br.com.erivelton.pix.chave.dto.requisicao.ChaveASerRemovidaRequisicao
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.shared.apiexterna.ApiExternaBCB
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.DeletaChavePixRequisicao
import br.com.erivelton.pix.shared.excecao.ChavePixNaoEncontradaException
import br.com.erivelton.pix.shared.excecao.OperacaoNaoAutorizadaException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class RemovaPixServico(
    @Inject val repositorio: ChaveRepositorio,
    @Inject val apiExternaBCB: ApiExternaBCB
) {

    fun remova(@Valid request: ChaveASerRemovidaRequisicao) {
        val chaveASerRemovida = repositorio
            .findByIdAndClienteId(request.id!!.toLong(), request.clienteId!!)
            .orElseThrow { ChavePixNaoEncontradaException("Chave pix não foi encontrada ou usuário inválido!!") }

        repositorio.delete(chaveASerRemovida)

        val chavePixDeletada = DeletaChavePixRequisicao(chaveASerRemovida)

        val respostaBCB = apiExternaBCB.removePix(chavePixDeletada, chavePixDeletada.key)

        if(respostaBCB.status == HttpStatus.FORBIDDEN){
            throw OperacaoNaoAutorizadaException("Proibido realizar a operação")
        }

        if(respostaBCB.status == HttpStatus.NOT_FOUND){
            throw ChavePixNaoEncontradaException("Chave pix não foi encontrada")
        }
    }
}