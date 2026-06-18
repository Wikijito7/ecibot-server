package es.wokis.data.mapper.sound

import es.wokis.data.bo.sound.ReactionBO
import es.wokis.data.bo.sound.SoundBO
import es.wokis.data.bo.sound.SoundUserBO
import es.wokis.data.dbo.ReactionDBO
import es.wokis.data.dbo.SoundDBO
import es.wokis.data.dbo.SoundUserDBO
import es.wokis.data.dto.sound.ReactionDTO
import es.wokis.data.dto.sound.SoundDTO
import es.wokis.data.dto.sound.SoundUserDTO

// region dto to bo
@JvmName("soundDTOToBO")
fun List<SoundDTO>.toBO() = this.map { it.toBO() }

fun SoundDTO.toBO() = SoundBO(
    id = id,
    displayId = displayId,
    title = title,
    description = description,
    soundUrl = soundUrl,
    createdBy = createdBy,
    thumbsUp = thumbsUp.toBO(),
    thumbsDown = thumbsDown.toBO(),
    createdOn = createdOn,
    status = status,
    reactions = reactions.toBO(),
)

@JvmName("soundUserDTOToBO")
fun List<SoundUserDTO>.toBO() = this.map { it.toBO() }

fun SoundUserDTO.toBO() = SoundUserBO(
    id = id,
    displayName = displayName
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
    description = description,
    soundUrl = soundUrl,
    createdBy = createdBy,
    thumbsUp = thumbsUp.toDTO(),
    thumbsDown = thumbsDown.toDTO(),
    createdOn = createdOn,
    status = status,
    reactions = reactions.toDTO()
)

@JvmName("soundUserBOToDTO")
fun List<SoundUserBO>.toDTO() = this.map { it.toDTO() }

fun SoundUserBO.toDTO() = SoundUserDTO(
    id = id,
    displayName = displayName
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
    description = description,
    soundUrl = soundUrl,
    createdBy = createdBy,
    thumbsUp = thumbsUp.toBO(),
    thumbsDown = thumbsDown.toBO(),
    createdOn = createdOn,
    status = status,
    reactions = reactions.toBO(),
)

@JvmName("soundUserDBOToBO")
fun List<SoundUserDBO>.toBO() = this.map { it.toBO() }

fun SoundUserDBO.toBO() = SoundUserBO(
    id = id,
    displayName = displayName
)

@JvmName("reactionDBOToBO")
fun List<ReactionDBO>.toBO() = this.map { it.toBO() }

fun ReactionDBO.toBO() = ReactionBO(
    unicode = unicode,
    addedBy = addedBy
)

// endregion

// region bo to dbo

@JvmName("soundBOToDBO")
fun List<SoundBO>.toDBO() = this.map { it.toDBO() }

fun SoundBO.toDBO() = SoundDBO(
    id = id,
    displayId = displayId,
    title = title,
    description = description,
    soundUrl = soundUrl,
    createdBy = createdBy,
    thumbsUp = thumbsUp.toDBO(),
    thumbsDown = thumbsDown.toDBO(),
    createdOn = createdOn,
    status = status,
    reactions = reactions.toDBO(),
)

@JvmName("soundUserBOToDBO")
fun List<SoundUserBO>.toDBO() = this.map { it.toDBO() }

fun SoundUserBO.toDBO() = SoundUserDBO(
    id = id,
    displayName = displayName
)

@JvmName("reactionBOToDBO")
fun List<ReactionBO>.toDBO() = this.map { it.toDBO() }

fun ReactionBO.toDBO() = ReactionDBO(
    unicode = unicode,
    addedBy = addedBy
)

// endregion
