package br.com.erivelton.pix.chave.repositorio

import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.entidade.Conta
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChaveRepositorio : JpaRepository<Chave, Long>{
    fun existsByValor(valor: String): Boolean

    fun findByIdAndClienteId(id: Long, clienteId: String): Optional<Chave>
    fun findByValor(valor: String): Chave?
}