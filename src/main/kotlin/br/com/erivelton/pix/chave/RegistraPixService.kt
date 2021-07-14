package br.com.erivelton.pix.chave

import br.com.erivelton.pix.erro.ChavePixDuplicadaException
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class RegistraPixService(val chaveRepositorio: ChaveRepositorio) {

    fun salvarPix(@Valid novoPix: NovoPix): Chave {
        if(chaveRepositorio.existsByValor(novoPix.valorChave)){
            throw ChavePixDuplicadaException("Chave pix já foi cadastrada para o usuário!!")
        }
        val chave = novoPix.paraEntidadeChave()
        val chaveSalva = chaveRepositorio.save(chave)
        return chaveSalva
    }

}