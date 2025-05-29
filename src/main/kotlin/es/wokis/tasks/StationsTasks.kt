package es.wokis.tasks

import es.wokis.data.repository.radio.RadioRepository
import io.ktor.server.application.Application
import io.ktor.server.application.log
import kotlinx.coroutines.delay
import org.koin.ktor.ext.inject

private const val RADIO_AUTO_FETCH_TIMEOUT = 24 * 60 * 60 + 10 * 1000L

suspend fun Application.registerPeriodicStationRequest() {
    val radioRepository by inject<RadioRepository>()

    while (true) {
        log.info("Auto fetching remote radios")
        radioRepository.fetchAndSaveRemoteRadios()
        delay(RADIO_AUTO_FETCH_TIMEOUT)
    }
}