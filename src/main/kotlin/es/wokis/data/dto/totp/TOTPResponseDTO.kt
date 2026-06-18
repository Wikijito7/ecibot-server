package es.wokis.data.dto.totp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TOTPResponseDTO(
    @SerialName("encodedSecret")
    val encodedSecret: String,
    @SerialName("totpUrl")
    val totpUrl: String,
    @SerialName("words")
    val words: List<String>
)
