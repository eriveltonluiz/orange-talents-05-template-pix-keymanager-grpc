package br.com.erivelton.pix.chave

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
}
