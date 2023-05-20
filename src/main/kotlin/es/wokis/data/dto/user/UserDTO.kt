package es.wokis.data.dto.user

import com.google.gson.annotations.SerializedName
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import java.util.*

data class UserDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("image")
    val image: String = EMPTY_TEXT,
    @SerializedName("lang")
    val lang: String,
    @SerializedName("createdOn")
    val createdOn: Long,
    @SerializedName("totpEnabled")
    val totpEnabled: Boolean,
    @SerializedName("emailVerified")
    val emailVerified: Boolean = false,
)