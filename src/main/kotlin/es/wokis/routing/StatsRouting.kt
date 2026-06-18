package es.wokis.routing

import es.wokis.data.mapper.stats.toDTO
import es.wokis.data.repository.StatsRepository
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpStatsRouting() {
    val statsRepository by inject<StatsRepository>()

    authenticate {
        get("/stats") {
            call.respond(statsRepository.getStats().toDTO())
        }
    }
}