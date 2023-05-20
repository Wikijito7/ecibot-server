package es.wokis.data.dto.response

import com.google.gson.annotations.SerializedName

data class AcknowledgeDTO(
    @SerializedName("acknowledge")
    val acknowledge: Boolean
)