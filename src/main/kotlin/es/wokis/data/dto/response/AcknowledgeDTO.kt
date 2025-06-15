package es.wokis.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcknowledgeDTO(
    @SerialName("acknowledge")
    val acknowledge: Boolean
)
