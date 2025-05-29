package es.wokis.plugins

import es.wokis.tasks.registerPeriodicStationRequest
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Application.configureTasks() {
    launch(Dispatchers.IO) {
        registerPeriodicStationRequest()
    }
}