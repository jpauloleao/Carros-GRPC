package br.com.zup.edu

import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarroEndpoint(@Inject val carroRepository: CarroRepository):CarrosGrpcTesteIntegracaoServiceGrpc.CarrosGrpcTesteIntegracaoServiceImplBase(){

    override fun adicionar(request: CarroRequest, responseObserver: StreamObserver<CarroResponse>?) {
        if(carroRepository.existsByPlaca(request.placa)){
            responseObserver?.onError(Status.ALREADY_EXISTS
                .withDescription("Carro com placa existente")
                .asRuntimeException())
            return
        }
        val carro = Carro(
            modelo = request.modelo,
            placa = request.placa
        )
        try {
            carroRepository.save(carro)
        }
        catch (e: ConstraintViolationException){
            responseObserver?.onError(Status.INVALID_ARGUMENT
                .withDescription("dados invalidos")
                .asRuntimeException())
            return
        }

        responseObserver?.onNext(CarroResponse.newBuilder().setId(carro.id!!).build())
        responseObserver?.onCompleted()
    }
}
