package br.com.erivelton.pix.shared.excecao

import java.lang.RuntimeException

class ChavePixNaoEncontradaException(mensagem: String) : RuntimeException(mensagem)