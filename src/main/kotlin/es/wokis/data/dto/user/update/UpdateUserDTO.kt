package es.wokis.data.dto.user.update

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDTO(
    @SerialName("username")
    val username: String?,
    @SerialName("email")
    val email: String?,
)
