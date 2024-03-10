package es.wokis.routing

import es.wokis.data.repository.sound.SoundRepository
import es.wokis.utils.user
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpSoundRouting() {
    val repository by inject<SoundRepository>()
    authenticate {
        get("/sounds") {

        }

        route("/sound") {
            post {
                val multipartData = call.receiveMultipart()
                val callUser = call.user
                callUser?.let { user ->
                    val sounds = multipartData.readAllParts()
                        .filterIsInstance<PartData.FileItem>()
                        .filter {
                            it.contentType.toString().startsWith("audio")
                        }
                    repository.addRawSounds(sounds, user)
                }
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

                post("/upvote") {
                    val callUser = call.user
                    val id = call.parameters["id"]
                    callUser?.let { user ->
                        repository.upVoteSound(user, id)
                    }
                }

                post("/downvote") {
                    val callUser = call.user
                    val id = call.parameters["id"]
                    callUser?.let { user ->
                        repository.downVoteSound(user, id)
                    }
                }

                post("/rawSound") {
                    val multipartData = call.receiveMultipart()
                    val callUser = call.user
                    callUser?.let { user ->
                        val sound = multipartData.readAllParts()
                            .filterIsInstance<PartData.FileItem>()
                            .find {
                                it.contentType.toString().startsWith("audio")
                            }
                            ?: run {
                                call.respond(HttpStatusCode.NotFound, "No sound found")
                                return@let
                            }
                        repository.updateRawSound(sound, user)
                    }
                }
            }
        }
    }
}