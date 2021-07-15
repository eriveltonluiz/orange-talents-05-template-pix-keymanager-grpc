package br.com.erivelton.pix.shared.apiexterna

import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.DadosChavePixRequisicao
import br.com.erivelton.pix.shared.apiexterna.dto.requisicao.DeletaChavePixRequisicao
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.ChavePixCriadaResposta
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.ChavePixDeletadaResposta
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(value = "\${api.bcb}")
interface ApiExternaBCB {

    @Post(produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun registraPix(@Body dadosChavePixRequisicao: DadosChavePixRequisicao) : HttpResponse<ChavePixCriadaResposta>

    @Delete(value = "/{key}", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun removePix(@Body deletaChavePixRequisicao: DeletaChavePixRequisicao, @PathVariable key: String): HttpResponse<ChavePixDeletadaResposta>
}