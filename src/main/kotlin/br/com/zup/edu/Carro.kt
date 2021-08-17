package br.com.zup.edu


import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank


@Entity
data class Carro(
    @field:NotBlank val modelo: String,
    @field:NotBlank val placa: String
) {
    @Id
    @GeneratedValue
    val id: Long? = null

}
