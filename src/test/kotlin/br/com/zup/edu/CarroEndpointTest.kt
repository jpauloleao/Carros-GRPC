package br.com.zup.edu

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarroEndpointTest(val repository: CarroRepository, val grpcClient : CarrosGrpcTesteIntegracaoServiceGrpc.CarrosGrpcTesteIntegracaoServiceBlockingStub){

    //1.happy patch
    //2.quando já existe carro com placa
    //3.quando dados são invalidos

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `deve adicionar novo carro`(){
        //cenario

        //açao
        val response = grpcClient.adicionar(CarroRequest.newBuilder()
                                            .setModelo("Gol")
                                            .setPlaca("HWV-2100")
                                            .build())
        //validacao
        with(response){
            assertNotNull(id)
            assertTrue(repository.existsById(response.id )) //efeito colateral
        }
    }

    @Test
    fun `nao deve adicionar novo carro quando carro com placa já existente`(){
        //cenario
        val existente = repository.save(Carro("Palio", "OIP-111"))

        //acao
        val erro = assertThrows<StatusRuntimeException>{
            grpcClient.adicionar(CarroRequest.newBuilder()
                .setModelo("Gol")
                .setPlaca("OIP-111")
                .build())

        }

        //validacao
        with(erro){
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Carro com placa existente", this.status.description)
        }
    }

    @Test
    fun `nao deve adicionar novo carro quando carro quando dados de entrada forem invalidos`(){
        //cenario

        //acao
        val erro = assertThrows<StatusRuntimeException>{
            grpcClient.adicionar(CarroRequest.newBuilder()
                .setModelo("")
                .setPlaca("")
                .build())

        }

        //validacao
        with(erro){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("dados invalidos", this.status.description)
        }
    }

    //Client GRPC
    @Factory
    class Clients{
        @Singleton
        fun blokingStub(@GrpcChannel(GrpcServerChannel.NAME) channel : ManagedChannel ): CarrosGrpcTesteIntegracaoServiceGrpc.CarrosGrpcTesteIntegracaoServiceBlockingStub? {
            return CarrosGrpcTesteIntegracaoServiceGrpc.newBlockingStub(channel)
        }
    }

}