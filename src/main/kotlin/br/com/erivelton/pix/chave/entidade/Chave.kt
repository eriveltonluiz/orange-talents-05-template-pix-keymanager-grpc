package br.com.erivelton.pix.chave.entidade

import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.shared.apiexterna.dto.enums.TypePerson
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DetalhesChavePixResposta
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.persistence.*

@Entity
class Chave(

    @Column(name = "cliente_id")
    val clienteId: String,

    var valor: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_chave")
    val tipoChave: TipoChave,

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta")
    val tipoConta: TipoConta,

    @Embedded val conta: Conta,
)
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var criadoEm: LocalDateTime? = null

    @Enumerated(EnumType.STRING)
    var tipoCliente: TypePerson? = null

    fun idClienteItau(): String? {
        return clienteId.toString()
    }

    fun atualizaComDadosDoBancoCentral(chavePixCriadaResposta: DetalhesChavePixResposta){
//        criadoEm = LocalDateTime.parse(chavePixCriadaResposta.createdAt)
        criadoEm = chavePixCriadaResposta.createdAt
        tipoCliente = chavePixCriadaResposta.tipoCliente()
        valor = chavePixCriadaResposta.key
    }

    fun ispbItau(): String{
        return conta.ispb
    }

    fun instituicaoDoCliente(): String{
        return conta.instituicao
    }

    fun paraNano(): Int {
        return criadoEm!!.toInstant(ZoneOffset.UTC).nano
    }

    fun paraSeconds(): Long {
        return criadoEm!!.toInstant(ZoneOffset.UTC).epochSecond
    }
}
