package es.wokis.data.dbo

import org.bson.codecs.pojo.annotations.BsonId

data class SoundDBO(
    @BsonId
    val id: Long? = null,
    val displayId: String,
    val title: String,
    val soundUrl: String,
    val createdBy: String,
    val thumbsUp: Int,
    val thumbsDown: Int,
    val createdOn: Long,
    val reactions: List<ReactionDBO> = emptyList()
)

data class ReactionDBO(
    val unicode: String,
    val addedBy: String
)