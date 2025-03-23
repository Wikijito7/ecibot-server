package es.wokis.data.dto.radio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioDTO(
    @SerialName("radioName")
    val radioName: String,
    @SerialName("url")
    val url: String,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerialName("countryCode")
    val countryCode: String
)