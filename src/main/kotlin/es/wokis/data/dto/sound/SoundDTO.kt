package es.wokis.data.dto.sound

import com.google.gson.annotations.SerializedName
import es.wokis.utils.HashGenerator.generateHashWithSeed

data class SoundDTO(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("displayId")
    val displayId: String = generateHashWithSeed(),
    @SerializedName("title")
    val title: String,
    @SerializedName("sound")
    val soundUrl: String,
    @SerializedName("createdBy")
    val createdBy: String,
    @SerializedName("thumbsUp")
    val thumbsUp: Int,
    @SerializedName("thumbsDown")
    val thumbsDown: Int,
    @SerializedName("createdOn")
    val createdOn: Long,
    @SerializedName("reactions")
    val reactions: List<ReactionDTO>
)

data class ReactionDTO(
    @SerializedName("unicode")
    val unicode: String,
    @SerializedName("userId")
    val addedBy: String
)

data class SoundRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("sound")
    val sound: String
)
