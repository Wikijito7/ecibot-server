package es.wokis.routing

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Routing.setUpStatsRouting() {
    authenticate {
        get("/stats") {

        }
    }
}