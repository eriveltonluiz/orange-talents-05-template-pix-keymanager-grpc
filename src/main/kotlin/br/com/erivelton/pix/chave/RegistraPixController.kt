package br.com.erivelton.pix.chave

import br.com.erivelton.pix.PixRequest
import io.micronaut.http.annotation.*

@Controller
class RegistraPixController(val apiExternaContasItau: ApiExternaContasItau) {

//    @Get(value = "/chaves/{clienteId}")
//    fun registra(@PathVariable clienteId: String, @QueryValue tipo: String){
//        println(clienteId)
//        println(tipo)
//        val consultaCliente = apiExternaContasItau.consultaCliente(clienteId, tipo)
//
//        println(consultaCliente.body())
//    }

    @Post(value = "/api/chaves")
    fun registra(@Body dadosRequest: PixRequest){
        val consultaCliente = apiExternaContasItau.consultaCliente(dadosRequest.clienteId, "dadosRequest")

        println(dadosRequest.clienteId)
        println(consultaCliente.body())
    }
}