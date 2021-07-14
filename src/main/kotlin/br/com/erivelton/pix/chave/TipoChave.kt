package br.com.erivelton.pix.chave

enum class TipoChave {
    CHAVE_DESCONHECIDA{
        override fun validaChave(valor: String): Boolean {
            println("teste 1")
            return false
        }
    },
    CPF{
        override fun validaChave(valor: String): Boolean {
            println("teste2")
            if(valor.matches("^[0-9]{11}$".toRegex())){
                return true
            }
            return false
        }
    },
    CELULAR{
        override fun validaChave(valor: String): Boolean {
            println("teste3")
            if(valor.matches("^\\+[1-9][0-9]\\d{1,14}$".toRegex())){
                return true
            }
            return false
        }
    },
    EMAIL{
        override fun validaChave(valor: String): Boolean {
            println("teste4")
            if(!valor.isNotBlank()) {
                return false
            }
            return true
        }
    },
    CHAVE_ALEATORIA{
        override fun validaChave(valor: String): Boolean {
            if(valor.isNotBlank()) {
                return false
            }
            return true
        }
    };

    abstract fun validaChave(valor: String): Boolean
}