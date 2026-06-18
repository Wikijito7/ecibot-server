package es.wokis

import com.typesafe.config.ConfigFactory
import es.wokis.plugins.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    var host = ""
    var port = 0
    embeddedServer(
        Netty,
        environment = applicationEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
            host = config.host
            port = config.port
        },
        configure = {
            connector {
                this.host = host
                this.port = port
            }
        }
    ).start(wait = true)
}

fun Application.module() {
    initConfig()
    configureKoin()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRateLimit()
    configureRouting()
    configureTasks()
}
