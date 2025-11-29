package es.wokis.data.dto.user.auth

import es.wokis.data.constants.ServerConstants.DEFAULT_LANG
import es.wokis.services.GOOGLE_AUTHENTICATOR
import kotlinx.serialization.SerialName

data class LoginDTO(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String,
    val isGoogleAuth: Boolean = false
)

data class RegisterDTO(
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("lang")
    val lang: String = DEFAULT_LANG,
    val isGoogleAuth: Boolean = false
)

data class AuthResponseDTO(
    @SerialName("authToken")
    val authToken: String
)

data class GoogleAuthDTO(
    @SerialName("authToken")
    val authToken: String
)

data class ChangePassRequestDTO(
    @SerialName("oldPass")
    val oldPass: String?,
    @SerialName("recoverCode")
    val recoverCode: String?,
    @SerialName("newPass")
    val newPass: String
)

data class TOTPRequestDTO(
    @SerialName("authType")
    val authType: String = GOOGLE_AUTHENTICATOR,
    @SerialName("timestamp")
    val timestamp: Long,
)
