package es.wokis.routing

import es.wokis.data.repository.sound.SoundRepository
import es.wokis.utils.getAllParts
import es.wokis.utils.user
import io.ktor.http.*
import io.ktor.http.content.*
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
                    val sounds = multipartData.getAllParts()
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
                    val soundId = call.parameters["id"]
                    callUser?.let { user ->
                        soundId?.let {
                            repository.upVoteSound(user, soundId)
                        }
                    }
                }

                post("/downvote") {
                    val callUser = call.user
                    val soundId = call.parameters["id"]
                    callUser?.let { user ->
                        soundId?.let {
                            repository.downVoteSound(user, soundId)
                        }
                    }
                }

                post("/rawSound") {
                    val multipartData = call.receiveMultipart()
                    val callUser = call.user
                    callUser?.let { user ->
                        val sound = multipartData.getAllParts()
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