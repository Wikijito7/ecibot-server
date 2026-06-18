package es.wokis.data.dbo

import org.bson.codecs.pojo.annotations.BsonId

data class SoundDBO(
    @BsonId
    val id: Long? = null,
    val displayId: String,
    val title: String,
    val description: String = "",
    val soundUrl: String,
    val createdBy: String,
    val thumbsUp: List<SoundUserDBO> = emptyList(),
    val thumbsDown: List<SoundUserDBO> = emptyList(),
    val createdOn: Long,
    val status: String = "pending",
    val reactions: List<ReactionDBO> = emptyList()
)

data class SoundUserDBO(
    val id: String,
    val displayName: String
)

data class ReactionDBO(
    val unicode: String,
    val addedBy: String
)