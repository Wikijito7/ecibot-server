package es.wokis.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimit() {
    install(RateLimit) {
        global {
            rateLimiter(limit = 120, refillPeriod = 60.seconds)
        }

        register(RateLimitName("auth")) {
            rateLimiter(10, refillPeriod = 60.seconds)
        }
    }
}