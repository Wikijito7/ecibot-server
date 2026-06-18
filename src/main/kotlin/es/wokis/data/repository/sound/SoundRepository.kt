package es.wokis.data.repository.sound

import es.wokis.data.bo.sound.SoundBO
import es.wokis.data.bo.sound.SoundUserBO
import es.wokis.data.bo.user.UserBO
import es.wokis.data.datasource.local.sound.SoundsLocalDataSource
import es.wokis.data.exception.SoundNotFoundException
import es.wokis.data.mapper.sound.toBO
import es.wokis.data.mapper.sound.toDBO
import es.wokis.services.SoundFileService
import es.wokis.utils.HashGenerator
import io.ktor.http.content.*

interface SoundRepository {
    suspend fun getSounds(page: Int, limit: Int): List<SoundBO>
    suspend fun getSoundsCount(): Long
    suspend fun getSoundById(displayId: String): SoundBO?
    suspend fun getUserSounds(userId: String, page: Int, limit: Int): List<SoundBO>
    suspend fun getUserSoundsCount(userId: String): Long
    suspend fun createSounds(
        title: String,
        description: String,
        audioFiles: List<PartData.FileItem>,
        user: UserBO
    ): List<SoundBO>
    suspend fun updateRawSound(displayId: String, audioFile: PartData.FileItem, user: UserBO): Boolean
    suspend fun updateSound(
        displayId: String,
        title: String?,
        description: String?,
        user: UserBO
    ): Boolean
    suspend fun removeSound(displayId: String, user: UserBO): Boolean
    suspend fun voteSound(user: UserBO, displayId: String, vote: String): Boolean
}

class SoundRepositoryImpl(
    private val soundsLocalDataSource: SoundsLocalDataSource,
) : SoundRepository {

    override suspend fun getSounds(page: Int, limit: Int): List<SoundBO> =
        soundsLocalDataSource.getAllSounds(page, limit)

    override suspend fun getSoundsCount(): Long =
        soundsLocalDataSource.getSoundsCount()

    override suspend fun getSoundById(displayId: String): SoundBO? =
        soundsLocalDataSource.getSoundByDisplayId(displayId)

    override suspend fun getUserSounds(userId: String, page: Int, limit: Int): List<SoundBO> =
        soundsLocalDataSource.getSoundsByCreatedBy(userId, page, limit)

    override suspend fun getUserSoundsCount(userId: String): Long =
        soundsLocalDataSource.getSoundsByCreatedByCount(userId)

    override suspend fun createSounds(
        title: String,
        description: String,
        audioFiles: List<PartData.FileItem>,
        user: UserBO
    ): List<SoundBO> {
        return audioFiles.mapNotNull { audioFile ->
            val displayId = HashGenerator.generateHashWithSeed()
            val soundUrl = SoundFileService.saveSound(displayId, audioFile)
            val now = System.currentTimeMillis()
            val sound = SoundBO(
                displayId = displayId,
                title = title,
                description = description,
                soundUrl = soundUrl,
                createdBy = user.id ?: "",
                createdOn = now,
                status = "pending"
            )
            val created = soundsLocalDataSource.createSound(sound)
            if (created) sound else null
        }
    }

    override suspend fun updateRawSound(
        displayId: String,
        audioFile: PartData.FileItem,
        user: UserBO
    ): Boolean {
        val existing = soundsLocalDataSource.getSoundByDisplayId(displayId)
            ?: throw SoundNotFoundException
        SoundFileService.deleteSoundFiles(displayId)
        val soundUrl = SoundFileService.saveSound(displayId, audioFile)
        return soundsLocalDataSource.updateSound(existing.copy(soundUrl = soundUrl))
    }

    override suspend fun updateSound(
        displayId: String,
        title: String?,
        description: String?,
        user: UserBO
    ): Boolean {
        val existing = soundsLocalDataSource.getSoundByDisplayId(displayId)
            ?: throw SoundNotFoundException
        val updated = existing.copy(
            title = title ?: existing.title,
            description = description ?: existing.description
        )
        return soundsLocalDataSource.updateSound(updated)
    }

    override suspend fun removeSound(displayId: String, user: UserBO): Boolean {
        val existing = soundsLocalDataSource.getSoundByDisplayId(displayId)
            ?: throw SoundNotFoundException
        SoundFileService.deleteSoundFiles(displayId)
        return soundsLocalDataSource.deleteSound(displayId)
    }

    override suspend fun voteSound(user: UserBO, displayId: String, vote: String): Boolean {
        val existing = soundsLocalDataSource.getSoundByDisplayId(displayId)
            ?: throw SoundNotFoundException
        val userId = user.id ?: return false
        val voter = SoundUserBO(id = userId, displayName = user.username)
        val thumbsUp = existing.thumbsUp.toMutableList()
        val thumbsDown = existing.thumbsDown.toMutableList()
        thumbsUp.removeAll { it.id == userId }
        thumbsDown.removeAll { it.id == userId }
        when (vote) {
            "up" -> thumbsUp.add(voter)
            "down" -> thumbsDown.add(voter)
        }
        return soundsLocalDataSource.updateSound(existing.copy(thumbsUp = thumbsUp, thumbsDown = thumbsDown))
    }
}
