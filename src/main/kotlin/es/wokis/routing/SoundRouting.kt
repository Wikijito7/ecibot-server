package es.wokis.routing

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Routing.setUpSoundRouting() {
    authenticate {
        get("/sounds") {

        }

        route("/sound") {
            post {

            }

            route("/{id}") {
                get {

                }

                post {

                }

                put {

                }

                delete {

                }
            }
        }
    }
}