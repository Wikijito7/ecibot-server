package es.wokis.data.dto.sound

import es.wokis.data.dto.user.UserDTO
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
    @SerialName("sound")
    val soundUrl: String,
    @SerialName("createdBy")
    val createdBy: String,
    @SerialName("thumbsUp")
    val thumbsUp: List<UserDTO>,
    @SerialName("thumbsDown")
    val thumbsDown: List<UserDTO>,
    @SerialName("createdOn")
    val createdOn: Long,
    @SerialName("reactions")
    val reactions: List<ReactionDTO>
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
    @SerialName("sound")
    val sound: String
)
