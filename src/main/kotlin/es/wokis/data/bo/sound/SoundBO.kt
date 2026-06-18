package es.wokis.data.bo.sound


data class SoundBO(
    val id: Long? = null,
    val displayId: String,
    val title: String,
    val description: String = "",
    val soundUrl: String,
    val createdBy: String,
    val thumbsUp: List<SoundUserBO> = emptyList(),
    val thumbsDown: List<SoundUserBO> = emptyList(),
    val createdOn: Long,
    val status: String = "pending",
    val reactions: List<ReactionBO> = emptyList()
)

data class SoundUserBO(
    val id: String,
    val displayName: String
)

data class ReactionBO(
    val unicode: String,
    val addedBy: String
)