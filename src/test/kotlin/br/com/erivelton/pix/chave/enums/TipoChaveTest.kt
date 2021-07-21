package br.com.erivelton.pix.chave.enums

import br.com.erivelton.pix.chave.enums.TipoChave
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest{

    @Nested
    inner class EMAIL {

        @Test
        internal fun `deve invalidar quando nao for inserido o valor da chave de EMAIL`() {
            with(TipoChave.EMAIL) {
                assertFalse(validaChave(""))
                assertFalse(validaChave(null))
            }
        }

        @Test
        internal fun `deve invalidar quando o formato de e-mail não for condizente`() {
            with(TipoChave.EMAIL) {
                assertFalse(validaChave("eriveltonemail.com"))
            }
        }

        @Test
        internal fun `deve validar quando o formato do e-mail for condizente`() {
            with(TipoChave.EMAIL) {
                assertTrue(validaChave("erivelton@gmail.com.br"))
            }
        }

    }

    @Nested
    inner class CPF{

        @Test
        internal fun `deve invalidar quando nao for inserido o valor da chave CPF`() {
            with(TipoChave.CPF) {
                assertFalse(validaChave(""))
                assertFalse(validaChave(null))
            }
        }

        @Test
        internal fun `deve invalidar quando o valor da chave CPF nao for condizente`() {
            with(TipoChave.CPF) {
                assertFalse(validaChave("2132123"))
            }
        }

        @Test
        internal fun `deve validar quando o número da chave CPF for condizente`() {
            with(TipoChave.CPF) {
                assertTrue(validaChave("21321232323"))
            }
        }

        @Test
        internal fun `deve invalidar quando houver embutido no valor da chave CPF letra(s)`() {
            with(TipoChave.CPF) {
                assertFalse(validaChave("2132123232a"))
            }
        }

    }

    @Nested
    inner class CELULAR{

        @Test
        internal fun `deve invalidar quando nao for inserido o valor da chave de CELULAR`() {
            with(TipoChave.PHONE) {
                assertFalse(validaChave(""))
                assertFalse(validaChave(null))
            }
        }

        @Test
        internal fun `deve invalidar quando o numero da chave CELULAR não for condizente`() {
            with(TipoChave.PHONE) {
                assertFalse(validaChave("00009999"))
            }
        }

        @Test
        internal fun `deve validar quando o numero da chave CELULAR for condizente`() {
            with(TipoChave.PHONE) {
                assertTrue(validaChave("+55988710913"))
            }
        }

        @Test
        internal fun `deve invalidar quando houver embutido no valor da chave CELULAR letra(s)`() {
            with(TipoChave.PHONE) {
                assertFalse(validaChave("+5598871091a"))
            }
        }

    }

    @Nested
    inner class RANDOM{

        @Test
        internal fun `deve invalidar quando o valor da chave ter sido inserido`() {
            with(TipoChave.RANDOM) {
                assertFalse(validaChave("eriveltonemail.com"))
            }
        }

        @Test
        internal fun `deve validar quando o valor da chave não for inserido`() {
            with(TipoChave.RANDOM) {
                assertTrue(validaChave(""))
                assertTrue(validaChave(null))
            }
        }

    }

}