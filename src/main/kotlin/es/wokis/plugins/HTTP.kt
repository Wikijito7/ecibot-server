package es.wokis.plugins

import es.wokis.services.TIMESTAMP_HEADER
import es.wokis.services.TOTPService
import es.wokis.services.TOTP_HEADER
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import java.time.Duration

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(TIMESTAMP_HEADER)
        allowHeader(TOTP_HEADER)

        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true
        maxAgeInSeconds = Duration.ofDays(1).toMinutes() * 60L

        anyHost()
    }
}
