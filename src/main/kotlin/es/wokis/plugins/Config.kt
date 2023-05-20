package es.wokis.plugins

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

private val configFile = File("config/", "app.conf")
private val wordsFile = File("config/", "words.txt")
lateinit var config: Config
lateinit var words: File

fun Application.initConfig() {
    // si no existe la config interna, tira una excepción
    val internalConf = this::class.java.getResourceAsStream("/app.conf") ?: throw IllegalAccessException()
    val internalWords = this::class.java.getResourceAsStream("/words.txt") ?: throw IllegalAccessException()

    if (!configFile.exists()) {
        configFile.mkdirs()
        Files.copy(internalConf, Paths.get(configFile.path), StandardCopyOption.REPLACE_EXISTING)
    }

    if (!wordsFile.exists()) {
        wordsFile.mkdirs()
        Files.copy(internalWords, Paths.get(wordsFile.path), StandardCopyOption.REPLACE_EXISTING)
    }

    config = ConfigFactory.parseFile(File("config/app.conf"))
    words = File("config/words.txt")
}

val Config.issuer: String
    get() = getString("issuer")
