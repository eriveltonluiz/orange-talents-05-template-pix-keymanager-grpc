package br.com.erivelton.pix.chave.entidade

import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import java.util.*
import javax.persistence.*

@Entity
class Chave(

    @Column(name = "cliente_id")
    val clienteId: UUID,

    val valor: String,

    @Column(name = "tipo_chave")
    val tipoChave: TipoChave,

    @Column(name = "tipo_conta")
    val tipoConta: TipoConta,

    @Embedded val conta: Conta,
)
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun idClienteItau(): String? {
        return clienteId.toString()
    }
}
