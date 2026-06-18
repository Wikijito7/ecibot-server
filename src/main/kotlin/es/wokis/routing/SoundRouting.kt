package es.wokis.routing

import es.wokis.data.dto.sound.UpdateSoundRequestDTO
import es.wokis.data.dto.sound.VoteRequestDTO
import es.wokis.data.exception.SoundNotFoundException
import es.wokis.data.mapper.sound.toDTO
import es.wokis.data.repository.sound.SoundRepository
import es.wokis.services.SoundFileService
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
            val page = call.request.queryParameters["page"]
                ?.toIntOrNull()
                ?.coerceAtLeast(1) ?: 1
            val limit = call.request.queryParameters["limit"]
                ?.toIntOrNull()
                ?.coerceIn(1, 100) ?: 20
            val status = call.request.queryParameters["status"]
                ?.takeIf { it.isNotBlank() }
            val sounds = repository.getSounds(page, limit, status)
            val total = repository.getSoundsCount(status)
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "data" to sounds.toDTO(),
                    "page" to page,
                    "limit" to limit,
                    "total" to total
                )
            )
        }

        get("/user/sounds") {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val page = call.request.queryParameters["page"]
                ?.toIntOrNull()
                ?.coerceAtLeast(1) ?: 1
            val limit = call.request.queryParameters["limit"]
                ?.toIntOrNull()
                ?.coerceIn(1, 100) ?: 20
            val userId = user.id ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val sounds = repository.getUserSounds(userId, page, limit)
            val total = repository.getUserSoundsCount(userId)
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "data" to sounds.toDTO(),
                    "page" to page,
                    "limit" to limit,
                    "total" to total
                )
            )
        }

        route("/sound") {
            post {
                val user = call.user ?: run {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val multipartData = call.receiveMultipart()
                val parts = multipartData.getAllParts()
                val title = parts
                    .filterIsInstance<PartData.FormItem>()
                    .firstOrNull { it.name == "title" }
                    ?.value ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Title is required")
                    return@post
                }
                val description = parts
                    .filterIsInstance<PartData.FormItem>()
                    .firstOrNull { it.name == "description" }
                    ?.value ?: ""
                val audioFiles = parts
                    .filterIsInstance<PartData.FileItem>()
                    .filter { it.contentType.toString().startsWith("audio") }

                if (audioFiles.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "No audio files provided")
                    return@post
                }

                val created = repository.createSounds(title, description, audioFiles, user)
                call.respond(HttpStatusCode.Created, created.toDTO())
            }

            route("/{id}") {
                get {
                    val displayId = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Id is required")
                        return@get
                    }
                    val sound = repository.getSoundById(displayId)
                        ?: run {
                            call.respond(HttpStatusCode.NotFound, "Sound not found")
                            return@get
                        }
                    call.respond(HttpStatusCode.OK, sound.toDTO())
                }

                put {
                    val user = call.user ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@put
                    }
                    val displayId = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Id is required")
                        return@put
                    }
                    val body = call.receive<UpdateSoundRequestDTO>()
                    if (body.title == null && body.description == null) {
                        call.respond(HttpStatusCode.BadRequest, "Nothing to update")
                        return@put
                    }
                    try {
                        repository.updateSound(displayId, body.title, body.description, user)
                        call.respond(HttpStatusCode.OK, "Sound updated")
                    } catch (e: SoundNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, "Sound not found")
                    }
                }

                delete {
                    val user = call.user ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@delete
                    }
                    val displayId = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Id is required")
                        return@delete
                    }
                    try {
                        repository.removeSound(displayId, user)
                        call.respond(HttpStatusCode.OK, "Sound deleted")
                    } catch (e: SoundNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, "Sound not found")
                    }
                }

                post("/vote") {
                    val user = call.user ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }
                    val displayId = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Id is required")
                        return@post
                    }
                    val body = call.receive<VoteRequestDTO>()
                    if (body.vote !in listOf("up", "down")) {
                        call.respond(HttpStatusCode.BadRequest, "Vote must be 'up' or 'down'")
                        return@post
                    }
                    try {
                        repository.voteSound(user, displayId, body.vote)
                        call.respond(HttpStatusCode.OK, "Vote recorded")
                    } catch (e: SoundNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, "Sound not found")
                    }
                }

                get("/file") {
                    val displayId = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Id is required")
                        return@get
                    }
                    val file = SoundFileService.getSoundFile(displayId)
                        ?: run {
                            call.respond(HttpStatusCode.NotFound, "Sound file not found")
                            return@get
                        }
                    call.respondFile(file)
                }

                post("/file") {
                    val user = call.user ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }
                    val displayId = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Id is required")
                        return@post
                    }
                    val multipartData = call.receiveMultipart()
                    val audioFile = multipartData
                        .getAllParts()
                        .filterIsInstance<PartData.FileItem>()
                        .firstOrNull { it.contentType.toString().startsWith("audio") }
                        ?: run {
                            call.respond(HttpStatusCode.BadRequest, "No audio file provided")
                            return@post
                        }
                    try {
                        repository.updateRawSound(displayId, audioFile, user)
                        call.respond(HttpStatusCode.OK, "Sound file updated")
                    } catch (e: SoundNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, "Sound not found")
                    }
                }
            }
        }
    }
}
