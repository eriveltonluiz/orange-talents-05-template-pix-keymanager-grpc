package br.com.erivelton.pix.shared.apiexterna

import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DadosClienteResposta
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${api.itau}/clientes/")
interface ApiExternaContasItau {

    @Get(value = "{clienteId}/contas")
    fun consultaCliente(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DadosClienteResposta>
}