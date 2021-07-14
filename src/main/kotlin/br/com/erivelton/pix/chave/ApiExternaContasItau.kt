package br.com.erivelton.pix.chave

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "http://localhost:9091/api/v1/clientes/")
interface ApiExternaContasItau {

    @Get(value = "{clienteId}/contas")
    fun consultaCliente(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DadosClienteResponse>
}