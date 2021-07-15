package br.com.erivelton.pix.chave.enums

import io.micronaut.validation.validator.constraints.EmailValidator

enum class TipoChave {
    CHAVE_DESCONHECIDA{
        override fun validaChave(valor: String?): Boolean {
            return false
        }
    },
    CPF{
        override fun validaChave(valor: String?): Boolean {
            if(valor.isNullOrBlank()) {
                return false
            }

            if(valor.matches("^[0-9]{11}$".toRegex())){
                return true
            }
            return false
        }
    },
    CELULAR{
        override fun validaChave(valor: String?): Boolean {
            if(valor.isNullOrBlank()) {
                return false
            }

            if(valor.matches("^\\+[1-9][0-9]\\d{1,14}$".toRegex())){
                return true
            }
            return false
        }
    },
    EMAIL{
        override fun validaChave(valor: String?): Boolean {
            if(valor.isNullOrBlank()) {
                return false
            }

            return EmailValidator().run {
                initialize(null)
                isValid(valor, null)
            }
        }
    },
    CHAVE_ALEATORIA{
        override fun validaChave(valor: String?): Boolean {
            return valor.isNullOrBlank()
        }
    };

    abstract fun validaChave(valor: String?): Boolean
}