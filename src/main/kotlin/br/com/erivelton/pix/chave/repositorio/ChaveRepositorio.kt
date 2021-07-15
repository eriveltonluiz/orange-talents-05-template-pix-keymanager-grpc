package br.com.erivelton.pix.chave.repositorio

import br.com.erivelton.pix.chave.entidade.Chave
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ChaveRepositorio : JpaRepository<Chave, Long>{
    fun existsByValor(valor: String): Boolean
}