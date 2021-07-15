package br.com.erivelton.pix.chave.servico

import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.dto.requisicao.NovoPix
import br.com.erivelton.pix.shared.apiexterna.ApiExternaBCB
import br.com.erivelton.pix.shared.excecao.ChavePixDuplicadaException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
@Transactional
class RegistraPixService(
    val chaveRepositorio: ChaveRepositorio,
    @Inject private val apiExternaBCB: ApiExternaBCB
) {
    fun salvarPix(@Valid novoPix: NovoPix): Chave {
        if(chaveRepositorio.existsByValor(novoPix.valorChave)){
            throw ChavePixDuplicadaException("Chave pix já foi cadastrada para o usuário!!")
        }
        val chave = novoPix.paraEntidadeChave()
        val chaveSalva = chaveRepositorio.save(chave)

        val dadosChavePixRequisicao = novoPix.paraBancoCentral()
        val respostaBCB = apiExternaBCB.registraPix(dadosChavePixRequisicao)

        if(respostaBCB.status != HttpStatus.CREATED){
            throw IllegalStateException("Banco central não conseguiu efetuar o registro da chave pix")
        }

        chaveSalva.atualizaComDadosDoBancoCentral(respostaBCB.body())

        return chaveSalva
    }

}