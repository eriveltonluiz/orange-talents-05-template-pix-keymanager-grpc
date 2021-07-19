package br.com.erivelton.pix.chave.servidor

import br.com.erivelton.pix.BuscaGrpcServiceGrpc
import br.com.erivelton.pix.DadosPixRequisicao
import br.com.erivelton.pix.InformacaoIdClienteRequisicao
import br.com.erivelton.pix.chave.dto.resposta.DetalhesChavePixLocalResposta
import br.com.erivelton.pix.chave.entidade.Chave
import br.com.erivelton.pix.chave.entidade.Conta
import br.com.erivelton.pix.chave.enums.TipoChave
import br.com.erivelton.pix.chave.enums.TipoConta
import br.com.erivelton.pix.chave.repositorio.ChaveRepositorio
import br.com.erivelton.pix.shared.apiexterna.ApiExternaBCB
import br.com.erivelton.pix.shared.apiexterna.dto.enums.AccountType
import br.com.erivelton.pix.shared.apiexterna.dto.enums.TypePerson
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.ClienteResposta
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.ContaBancariaResposta
import br.com.erivelton.pix.shared.apiexterna.dto.resposta.DetalhesChavePixResposta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class BuscaChavePixServidorTest(
    val repositorio: ChaveRepositorio,
    val grpcClient: BuscaGrpcServiceGrpc.BuscaGrpcServiceBlockingStub
){

    lateinit var dadosChavePixResposta: DetalhesChavePixResposta

    lateinit var clienteIdTeste: String

    @Inject
    lateinit var apiExternaBCB: ApiExternaBCB

    @BeforeEach
    fun setup(){
        repositorio.deleteAll()
        clienteIdTeste = "5260263c-a3c1-4727-ae32-3bdb2538841b"

        dadosChavePixResposta = DetalhesChavePixResposta(
            key = "+823713681230",
            keyType = TipoChave.PHONE,
            bankAccount = ContaBancariaResposta("60701190", "0001", "123455", AccountType.CACC),
            owner = ClienteResposta(TypePerson.NATURAL_PERSON, "Yuri Matheus", "86135457004"),
            createdAt = LocalDateTime.now()
        )
    }

    @Test
    internal fun `deve retornar o pix de acordo com a chave passada como argumento`() {
        repositorio.save(
            Chave(
                clienteIdTeste,
                "+823713681230",
                TipoChave.PHONE,
                TipoConta.CONTA_CORRENTE,
                Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
            )
        )

        Mockito.`when`(apiExternaBCB.buscaPix("+823713681230")).thenReturn(HttpResponse.ok(dadosChavePixResposta))

        val resposta = grpcClient.buscaPix(
            DadosPixRequisicao.newBuilder().setValorChave("+823713681230").build()
        )

        with(resposta){
            assertEquals("", clienteId)
            assertEquals("", pixId)
            assertEquals("+823713681230", valorChave)
        }
    }

    @Test
    internal fun `deve retornar o pix de acordo com a pix id e cliente id passados como argumentos`(){
        val save = repositorio.save(
            Chave(
                clienteIdTeste,
                "+823713681230",
                TipoChave.PHONE,
                TipoConta.CONTA_CORRENTE,
                Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
            )
        )

        Mockito.`when`(apiExternaBCB.buscaPix("+823713681230")).thenReturn(HttpResponse.ok(dadosChavePixResposta))

        println(save.id)
        val resposta = grpcClient.buscaPix(
            DadosPixRequisicao.newBuilder()
                .setClienteId(clienteIdTeste)
                .setPixId(5)
                .build()
        )

        with(resposta){
            assertEquals(clienteIdTeste, clienteId)
            assertEquals("5", pixId)
            assertEquals("+823713681230", valorChave)
        }
    }

    @Test
    internal fun `nao deve validar caso nao seja encontrada a chave pix pelos argumentos pix id e cliente id`() {
        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.buscaPix(DadosPixRequisicao.newBuilder()
                .setClienteId(clienteIdTeste)
                .setPixId(2)
                .build())
        }

        with(throwGerado){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Pix ID ou cliente não foram encontrados", status.description)
        }
    }

    @Test
    internal fun `nao deve validar caso nao seja encontrada a chave pix no banco central`(){
        Mockito.`when`(apiExternaBCB.buscaPix("+823713681230")).thenReturn(HttpResponse.notFound())

        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.buscaPix(DadosPixRequisicao.newBuilder().setValorChave("+823713681230").build())
        }

        throwGerado.printStackTrace()

        with(throwGerado){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não foi encontrada no BCB", status.description)
        }
    }

    @Test
    internal fun `nao deve validar caso os parametros estejam nulos ou vazios`() {

        val throwGerado = assertThrows<StatusRuntimeException>{
            grpcClient.buscaPix(
            DadosPixRequisicao.newBuilder().build())
        }
        with(throwGerado){
            assertEquals(Status.INVALID_ARGUMENT.code, throwGerado.status.code)
            assertEquals("busca.informacoesPix: Dados não condizentes", status.description)
        }
    }

    @Test
    internal fun `nao deve validar caso seja inserido uma chave e também o pix id`(){
        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.buscaPix(DadosPixRequisicao.newBuilder()
                .setValorChave("+55983261534")
                .setPixId(1L)
                .build())
        }

        with(throwGerado){
            assertEquals(Status.INVALID_ARGUMENT.code, throwGerado.status.code)
            assertEquals("busca.informacoesPix: Dados não condizentes", status.description)
        }
    }

    @Test
    internal fun `nao deve validar caso seja inserido uma chave e também o cliente id`(){
        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.buscaPix(DadosPixRequisicao.newBuilder()
                .setValorChave("+55983261534")
                .setClienteId(clienteIdTeste)
                .build())
        }

        with(throwGerado){
            assertEquals(Status.INVALID_ARGUMENT.code, throwGerado.status.code)
            assertEquals("busca.informacoesPix: Dados não condizentes", status.description)
        }
    }

    @Test
    internal fun `nao deve validar caso seja inserido somente o cliente id sem o pix id`() {
        val throwGerado = assertThrows<StatusRuntimeException>{
            grpcClient.buscaPix(DadosPixRequisicao.newBuilder()
                .setClienteId(clienteIdTeste)
                .build())
        }

        with(throwGerado){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("busca.informacoesPix: Dados não condizentes", status.description)
        }
    }

    @Test
    internal fun `deve listar todas as chaves pix`() {
        val chaves: List<Chave> = listOf(Chave(
                clienteIdTeste,
            "+823713681230",
                TipoChave.PHONE,
                TipoConta.CONTA_CORRENTE,
                Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
            ),
            Chave(
                clienteIdTeste,
                "+191385681230",
                TipoChave.PHONE,
                TipoConta.CONTA_POUPANCA,
                Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
            ),
            Chave(
                clienteIdTeste,
                "86135457004",
                TipoChave.CPF,
                TipoConta.CONTA_CORRENTE,
                Conta("ITAÚ UNIBANCO S.A.", "60701190", "0001", "123455", "Yuri Matheus", "86135457004")
            )
        )
        chaves[0].criadoEm = LocalDateTime.now()
        chaves[1].criadoEm = LocalDateTime.now()
        chaves[2].criadoEm = LocalDateTime.now()
        repositorio.saveAll(chaves)

        val resposta = grpcClient.buscaTodosPixCliente(InformacaoIdClienteRequisicao.newBuilder()
            .setClienteId(clienteIdTeste)
            .build())

        with(resposta){
            assertNotNull(this)
            assertEquals(clienteIdTeste, resposta.getPixGeralResposta(0).clienteId)
            assertEquals(2L, resposta.getPixGeralResposta(0).pixId.toLong())
            assertEquals(3, pixGeralRespostaList.size)
        }
    }

    @Test
    internal fun `deve retornar chave vazia caso cliente id não esteja cadastrado`(){
        val resposta = grpcClient.buscaTodosPixCliente(InformacaoIdClienteRequisicao.newBuilder()
            .setClienteId(clienteIdTeste + "a")
            .build())

        with(resposta){
            assertEquals(pixGeralRespostaList.size,0)
        }
    }

    @Test
    internal fun `nao deve retornar a lista caso cliente id seja nulo ou vazio`() {
        val throwGerado = assertThrows<StatusRuntimeException> {
            grpcClient.buscaTodosPixCliente(InformacaoIdClienteRequisicao.newBuilder()
                .build()
            )
        }

        with(throwGerado){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("buscaTodos.clienteId: must not be blank", status.description)
        }
    }

    @MockBean(ApiExternaBCB::class)
    fun bcbClient(): ApiExternaBCB{
        return Mockito.mock(ApiExternaBCB::class.java)
    }

    @Factory
    class Client{

        @Singleton
        fun blockingStubregistra(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): BuscaGrpcServiceGrpc.BuscaGrpcServiceBlockingStub{
            return BuscaGrpcServiceGrpc.newBlockingStub(channel)
        }

    }
}