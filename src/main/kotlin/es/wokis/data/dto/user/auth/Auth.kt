package es.wokis.data.dto.user.auth

import com.google.gson.annotations.SerializedName
import es.wokis.data.constants.ServerConstants.DEFAULT_LANG
import es.wokis.services.GOOGLE_AUTHENTICATOR

data class LoginDTO(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    val isGoogleAuth: Boolean = false
)

data class RegisterDTO(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("lang")
    val lang: String = DEFAULT_LANG,
    val isGoogleAuth: Boolean = false
)

data class AuthResponseDTO(
    @SerializedName("authToken")
    val authToken: String
)

data class GoogleAuthDTO(
    @SerializedName("authToken")
    val authToken: String
)

data class ChangePassRequestDTO(
    @SerializedName("oldPass")
    val oldPass: String?,
    @SerializedName("recoverCode")
    val recoverCode: String?,
    @SerializedName("newPass")
    val newPass: String
)

data class TOTPRequestDTO(
    @SerializedName("authType")
    val authType: String = GOOGLE_AUTHENTICATOR,
    @SerializedName("timestamp")
    val timestamp: Long,
)