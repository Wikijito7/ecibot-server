package es.wokis.data.bo.sound

data class SoundBO(
    val id: Long? = null,
    val displayId: String,
    val title: String,
    val soundUrl: String,
    val createdBy: String,
    val thumbsUp: Int,
    val thumbsDown: Int,
    val createdOn: Long,
    val reactions: List<ReactionBO> = emptyList()
)

data class ReactionBO(
    val unicode: String,
    val addedBy: String
)