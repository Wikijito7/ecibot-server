package es.wokis.data.dto.user

import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import kotlinx.serialization.SerialName

data class UserDTO(
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String,
    @SerialName("image")
    val image: String = EMPTY_TEXT,
    @SerialName("lang")
    val lang: String,
    @SerialName("createdOn")
    val createdOn: Long,
    @SerialName("totpEnabled")
    val totpEnabled: Boolean,
    @SerialName("emailVerified")
    val emailVerified: Boolean = false,
)
