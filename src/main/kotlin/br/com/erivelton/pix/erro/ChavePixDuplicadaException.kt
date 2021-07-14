package br.com.erivelton.pix.erro

import java.lang.RuntimeException

class ChavePixDuplicadaException(mensagem: String?): RuntimeException(mensagem)