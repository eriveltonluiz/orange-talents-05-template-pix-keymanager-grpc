package br.com.erivelton.pix.chave

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Conta(
    val instituicao: String,
    val ispb: String,
    val agencia: String,
    val numero: String,

    @Column(name = "nome_titular")
    val nomeTitular: String,

    @Column(name = "cpf_titular")
    val cpfTitular: String,
){

}
