package br.com.erivelton.pix.shared.excecao

import java.lang.RuntimeException

class ChavePixDuplicadaException(mensagem: String?): RuntimeException(mensagem)