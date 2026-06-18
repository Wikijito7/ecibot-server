package es.wokis.data.dto.recover

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecoverPassRequestDTO(
    @SerialName("email")
    val email: String
)
