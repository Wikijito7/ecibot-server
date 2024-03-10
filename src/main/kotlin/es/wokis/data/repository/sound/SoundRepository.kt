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