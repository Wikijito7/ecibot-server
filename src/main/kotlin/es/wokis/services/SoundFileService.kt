package es.wokis.services

import es.wokis.plugins.config
import es.wokis.utils.normalizeUrl
import io.ktor.http.content.*
import io.ktor.utils.io.*
import java.io.File

object SoundFileService {
    private val soundFolder by lazy { config.getString("soundFolder") }
    private val baseUri by lazy { config.getString("baseUri") }

    suspend fun saveSound(displayId: String, file: PartData.FileItem): String {
        val fileName = "$displayId.${getExtension(file)}"
        val soundPath = File("$soundFolder/$displayId", fileName).normalize()
        soundPath.parentFile.mkdirs()
        val channel = file.provider.invoke()
        val buffer = mutableListOf<Byte>()
        val temp = ByteArray(4096)
        while (!channel.isClosedForRead) {
            val read = channel.readAvailable(temp)
            if (read > 0) {
                buffer.addAll(temp.take(read))
            } else {
                break
            }
        }
        soundPath.writeBytes(buffer.toByteArray())
        return "$baseUri/sound/$displayId/file".normalizeUrl()
    }

    fun getSoundFile(displayId: String): File? {
        val dir = File("$soundFolder/$displayId")
        if (!dir.exists()) return null
        return dir.listFiles()?.firstOrNull()
    }

    fun deleteSoundFiles(displayId: String): Boolean {
        val dir = File("$soundFolder/$displayId")
        return if (dir.exists()) {
            dir.deleteRecursively()
        } else {
            true
        }
    }

    private fun getExtension(file: PartData.FileItem): String {
        val contentType = file.contentType?.toString() ?: "mp3"
        return when {
            contentType.contains("mpeg") -> "mp3"
            contentType.contains("ogg") -> "ogg"
            contentType.contains("wav") -> "wav"
            contentType.contains("flac") -> "flac"
            contentType.contains("aac") -> "aac"
            contentType.contains("webm") -> "webm"
            else -> "mp3"
        }
    }
}
