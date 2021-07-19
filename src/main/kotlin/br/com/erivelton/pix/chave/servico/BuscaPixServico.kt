package br.com.erivelton.pix.chave.servico

import br.com.erivelton.pix.chave.dto.requisicao.InformacoesChavePix
import br.com.erivelton.pix.chave.dto.resposta.DetalhesChavePixLocalResposta
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.shared.apiexterna.ApiExternaBCB
import br.com.erivelton.pix.shared.excecao.ChavePixNaoEncontradaException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class BuscaPixServico(
    val chaveRepositorio: ChaveRepositorio,
    val apiExternaBCB: ApiExternaBCB
    ) {

    fun busca(@Valid informacoesPix: InformacoesChavePix): DetalhesChavePixLocalResposta{
        var chaveEncontrada: Chave? = null
        var clienteId: String? = ""
        var pixId: String? = ""
        var chave = ""

        if(!informacoesPix.verificarClienteId()){
            chaveEncontrada = chaveRepositorio.findByIdAndClienteId(informacoesPix.pixId, informacoesPix.clienteId).orElseThrow {
                ChavePixNaoEncontradaException("Pix ID ou cliente não foram encontrados")
            }
            pixId = chaveEncontrada.id.toString()
            clienteId = chaveEncontrada.clienteId
            chave = chaveEncontrada.valor
        } else{
            chaveEncontrada = chaveRepositorio.findByValor(informacoesPix.chavePix)
        }

        val respostaBCB = apiExternaBCB.buscaPix(chave)
        if(respostaBCB.status == HttpStatus.NOT_FOUND){
            throw ChavePixNaoEncontradaException("Chave pix não foi encontrada no BCB")
        }

        val detalhesChavePixLocalResposta =
            DetalhesChavePixLocalResposta(respostaBCB.body(), chaveEncontrada?.instituicaoDoCliente())
        detalhesChavePixLocalResposta.pixId = pixId
        detalhesChavePixLocalResposta.clienteId = clienteId

        return detalhesChavePixLocalResposta
    }

    fun buscaTodos(@NotBlank clienteId: String): List<Chave>{
        return chaveRepositorio.findAllByClienteId(clienteId)
    }

}
