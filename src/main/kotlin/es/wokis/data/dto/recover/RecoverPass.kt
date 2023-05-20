package es.wokis.data.dto.recover

import com.google.gson.annotations.SerializedName

data class RecoverPassRequestDTO(
    @SerializedName("email")
    val email: String
)