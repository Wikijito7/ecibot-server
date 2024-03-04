package es.wokis.data.mapper.sound

import es.wokis.data.bo.sound.ReactionBO
import es.wokis.data.bo.sound.SoundBO
import es.wokis.data.dbo.ReactionDBO
import es.wokis.data.dbo.SoundDBO
import es.wokis.data.dto.sound.ReactionDTO
import es.wokis.data.dto.sound.SoundDTO

// region dto to bo
@JvmName("soundDTOToBO")
fun List<SoundDTO>.toBO() = this.map { it.toBO() }

fun SoundDTO.toBO() = SoundBO(
    id = id,
    displayId = displayId,
    title = title,
    soundUrl = soundUrl,
    createdBy = createdBy,
    thumbsUp = thumbsUp,
    thumbsDown = thumbsDown,
    createdOn = createdOn,
    reactions = reactions.toBO(),
)

@JvmName("reactionDTOToBO")
fun List<ReactionDTO>.toBO() = this.map { it.toBO() }

fun ReactionDTO.toBO() = ReactionBO(
    unicode = unicode,
    addedBy = addedBy
)

// endregion

// region bo to dto

@JvmName("soundBOToDTO")
fun List<SoundBO>.toDTO() = this.map { it.toDTO() }

fun SoundBO.toDTO() = SoundDTO(
    id = id,
    displayId = displayId,
    title = title,
    soundUrl = soundUrl,
    createdBy = createdBy,
    thumbsUp = thumbsUp,
    thumbsDown = thumbsDown,
    createdOn = createdOn,
    reactions = reactions.toDTO()
)

@JvmName("reactionBOToDTO")
fun List<ReactionBO>.toDTO() = this.map { it.toDTO() }

fun ReactionBO.toDTO() = ReactionDTO(
    unicode = unicode,
    addedBy = addedBy
)

// endregion

// region dbo to bo

@JvmName("soundDBOToBO")
fun List<SoundDBO>.toBO() = this.map { it.toBO() }

fun SoundDBO.toBO() = SoundBO(
    id = id,
    displayId = displayId,
    title = title,
    soundUrl = soundUrl,
    createdBy = createdBy,
    thumbsUp = thumbsUp,
    thumbsDown = thumbsDown,
    createdOn = createdOn,
    reactions = reactions.toBO(),
)

@JvmName("reactionDBOToBO")
fun List<ReactionDBO>.toBO() = this.map { it.toBO() }

fun ReactionDBO.toBO() = ReactionBO(
    unicode = unicode,
    addedBy = addedBy
)

// endregion

// region dbo to bo

@JvmName("soundBOToDBO")
fun List<SoundBO>.toDBO() = this.map { it.toDBO() }

fun SoundBO.toDBO() = SoundDBO(
    id = id,
    displayId = displayId,
    title = title,
    soundUrl = soundUrl,
    createdBy = createdBy,
    thumbsUp = thumbsUp,
    thumbsDown = thumbsDown,
    createdOn = createdOn,
    reactions = reactions.toDBO(),
)

@JvmName("reactionBOToDBO")
fun List<ReactionBO>.toDBO() = this.map { it.toDBO() }

fun ReactionBO.toDBO() = ReactionDBO(
    unicode = unicode,
    addedBy = addedBy
)

// endregion