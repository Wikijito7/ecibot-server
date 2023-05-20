package es.wokis.utils

import es.wokis.data.bo.user.UserBO
import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.mindrot.jbcrypt.BCrypt

val ApplicationCall.user: UserBO? get() = authentication.principal()

fun String?.orGeneratePassword(): String = this ?: generatePassword()

fun generatePassword(): String = BCrypt.hashpw(HashGenerator.generateHash(20), BCrypt.gensalt())