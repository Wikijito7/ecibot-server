package es.wokis.data.bo.sound

import es.wokis.data.bo.user.UserBO

data class SoundBO(
    val id: Long? = null,
    val displayId: String,
    val title: String,
    val soundUrl: String,
    val createdBy: String,
    val thumbsUp: List<UserBO>,
    val thumbsDown: List<UserBO>,
    val createdOn: Long,
    val reactions: List<ReactionBO> = emptyList()
)

data class ReactionBO(
    val unicode: String,
    val addedBy: String
)