package es.wokis.data.dto.user.update

import kotlinx.serialization.SerialName

data class UpdateUserDTO(
    @SerialName("username")
    val username: String?,
    @SerialName("email")
    val email: String?,
)
