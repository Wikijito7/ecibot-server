package es.wokis.data.dto.user.update

import com.google.gson.annotations.SerializedName

data class UpdateUserDTO(
    @SerializedName("username")
    val username: String?,
    @SerializedName("email")
    val email: String?,
)