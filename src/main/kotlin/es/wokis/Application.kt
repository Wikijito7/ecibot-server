package es.wokis

import com.typesafe.config.ConfigFactory
import es.wokis.plugins.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load("application.conf"))

        connector {
            host = config.host
            port = config.port
        }
    }).start(wait = true)
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
}
