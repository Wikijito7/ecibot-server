package es.wokis.routing

import es.wokis.data.dto.recover.RecoverPassRequestDTO
import es.wokis.data.dto.user.auth.AuthResponseDTO
import es.wokis.data.dto.user.auth.ChangePassRequestDTO
import es.wokis.data.dto.user.auth.LoginDTO
import es.wokis.data.dto.user.auth.RegisterDTO
import es.wokis.data.exception.TotpNotFoundException
import es.wokis.data.exception.UserNotFoundException
import es.wokis.data.mapper.acknowledge.toDTO
import es.wokis.data.mapper.user.toBO
import es.wokis.data.repository.recover.RecoverRepository
import es.wokis.data.repository.user.UserRepository
import es.wokis.data.repository.verify.VerifyRepository
import es.wokis.services.*
import es.wokis.utils.isEmail
import es.wokis.utils.user
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpAuthRouting() {
    val userRepository by inject<UserRepository>()
    val verifyRepository by inject<VerifyRepository>()
    val recoverRepository by inject<RecoverRepository>()
    val emailService by inject<EmailService>()

    rateLimit(RateLimitName("auth")) {
        post("/login") {
            val user = call.receive<LoginDTO>()
            val code = call.request.header(TOTP_HEADER)
            val timeStamp = call.request.header(TIMESTAMP_HEADER)?.toLongOrNull()
            try {
                val token: String? = userRepository.login(user, code, timeStamp)

                token?.let {
                    call.respond(HttpStatusCode.OK, AuthResponseDTO(it))
                } ?: run {
                    call.respond(HttpStatusCode.NotFound, "Wrong username or password")
                }

            } catch (exc: TotpNotFoundException) {
                respondNotAuthorization()

            } catch (exc: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post("/register") {
            val user = call.receive<RegisterDTO>()
            val token: String? = userRepository.register(user)
            token?.let {
                try {
                    emailService.sendVerifyEmail(user.toBO())

                } catch (exc: Exception) {
                    // no-op: as it isn't critical at this time
                }
                call.respond(HttpStatusCode.OK, AuthResponseDTO(it))

            } ?: run {
                call.respond(HttpStatusCode.Conflict, "That user already exists")
            }
        }

        get("/verify/{token}") {
            val token = call.parameters["token"]

            token?.let {
                try {
                    call.respond(verifyRepository.verify(token))

                } catch (exc: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        post("/recover") {
            try {
                val changePass: RecoverPassRequestDTO = call.receive()
                if (changePass.email.isEmail().not()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                call.respond(recoverRepository.requestChangePass(changePass.email).toDTO())

            } catch (e: UserNotFoundException) {
                call.respond(HttpStatusCode.Conflict)

            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/recover-pass") {
            try {
                val changePass: ChangePassRequestDTO = call.receive()
                call.respond(recoverRepository.changeUserPassword(changePass).toDTO())

            } catch (e: UserNotFoundException) {
                call.respond(HttpStatusCode.Conflict)

            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        authenticate {
            post("/verify") {
                val user = call.user
                user?.let {
                    try {
                        emailService.sendVerifyEmail(it)?.also { verification ->
                            call.respond(HttpStatusCode.OK, verification)

                        } ?: call.respond(HttpStatusCode.ServiceUnavailable)

                    } catch (exc: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, exc.stackTraceToString())
                    }


                } ?: call.respond(HttpStatusCode.ExpectationFailed)
            }

            post("/change-pass") {
                val user = call.user
                val changePass: ChangePassRequestDTO = call.receive()
                user?.let {
                    withAuthenticator(it) {
                        try {
                            call.respond(userRepository.changePass(user, changePass).toDTO())

                        } catch (exc: Exception) {
                            call.respond(HttpStatusCode.Conflict)
                        }
                    }
                } ?: call.respond(HttpStatusCode.ExpectationFailed)
            }

            post("/logout") {
                val user = call.user
                user?.let {
                    call.respond(userRepository.logout(user).toDTO())
                } ?: call.respond(HttpStatusCode.ExpectationFailed)
            }

            delete("/sessions") {
                val user = call.user
                user?.let {
                    call.respond(userRepository.closeAllSessions(user).toDTO())
                } ?: call.respond(HttpStatusCode.ExpectationFailed)
            }
        }
    }
}