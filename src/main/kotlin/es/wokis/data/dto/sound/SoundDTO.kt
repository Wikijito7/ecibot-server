package es.wokis.data.dto.sound

import es.wokis.utils.HashGenerator.generateHashWithSeed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoundDTO(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("displayId")
    val displayId: String = generateHashWithSeed(),
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String = "",
    @SerialName("sound")
    val soundUrl: String,
    @SerialName("createdBy")
    val createdBy: String,
    @SerialName("thumbsUp")
    val thumbsUp: List<SoundUserDTO> = emptyList(),
    @SerialName("thumbsDown")
    val thumbsDown: List<SoundUserDTO> = emptyList(),
    @SerialName("createdOn")
    val createdOn: Long,
    @SerialName("status")
    val status: String = "pending",
    @SerialName("reactions")
    val reactions: List<ReactionDTO> = emptyList()
)

@Serializable
data class SoundUserDTO(
    @SerialName("id")
    val id: String,
    @SerialName("displayName")
    val displayName: String
)

@Serializable
data class ReactionDTO(
    @SerialName("unicode")
    val unicode: String,
    @SerialName("userId")
    val addedBy: String
)

@Serializable
data class SoundRequestDTO(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String = "",
    @SerialName("sound")
    val sound: String
)

@Serializable
data class VoteRequestDTO(
    @SerialName("vote")
    val vote: String
)

@Serializable
data class UpdateSoundRequestDTO(
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null
)
