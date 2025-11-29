package es.wokis.data.repository.sound

import es.wokis.data.bo.sound.SoundBO
import es.wokis.data.bo.user.UserBO
import io.ktor.http.content.*

interface SoundRepository {
    fun addRawSounds(sounds: List<PartData.FileItem>, user: UserBO)
    fun updateRawSound(sound: PartData.FileItem, user: UserBO)
    fun addSound(sound: SoundBO, user: UserBO)
    fun updateSound(sound: SoundBO, user: UserBO)
    fun removeSound(sound: SoundBO, user: UserBO)
    fun upVoteSound(user: UserBO, id: String)
    fun downVoteSound(user: UserBO, id: String)
}

class SoundRepositoryImpl(
    // private val soundsLocalDataSource: SoundsLocalDataSource
) : SoundRepository {

    override fun addRawSounds(sounds: List<PartData.FileItem>, user: UserBO) {
        TODO("Not yet implemented")
    }

    override fun updateRawSound(sound: PartData.FileItem, user: UserBO) {
        TODO("Not yet implemented")
    }

    override fun addSound(sound: SoundBO, user: UserBO) {
        TODO("Not yet implemented")
    }

    override fun updateSound(sound: SoundBO, user: UserBO) {
        TODO("Not yet implemented")
    }

    override fun removeSound(sound: SoundBO, user: UserBO) {
        TODO("Not yet implemented")
    }

    override fun upVoteSound(user: UserBO, id: String) {
        TODO("Not yet implemented")
    }

    override fun downVoteSound(user: UserBO, id: String) {
        TODO("Not yet implemented")
    }
}
