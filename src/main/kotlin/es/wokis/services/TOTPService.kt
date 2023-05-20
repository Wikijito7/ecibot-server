package es.wokis.services

import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import es.wokis.data.bo.response.AcknowledgeBO
import es.wokis.data.bo.user.TOTPResponseBO
import es.wokis.data.bo.user.UserBO
import es.wokis.data.dto.user.auth.TOTPRequestDTO
import es.wokis.data.exception.TotpNotFoundException
import es.wokis.data.repository.user.UserRepository
import es.wokis.plugins.config
import es.wokis.plugins.issuer
import es.wokis.utils.HashGenerator
import es.wokis.utils.getRandomWords
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.StringUtils
import java.util.*

const val AUTHENTICATOR_HEADER = "WWW-Authenticate"
const val TIMESTAMP_HEADER = "timestamp"
const val GOOGLE_AUTHENTICATOR = "Google"
const val TOTP_HEADER = "2FA"

class TOTPService(private val userRepository: UserRepository) {
    /**
     * Sets up TOTP for User's account.
     * @throws IllegalStateException if repository cannot update user with TOTP
     * @return TOTPResponseBO with encodedSecret and totp url
     */
    suspend fun setUpTOTP(user: UserBO): TOTPResponseBO {
        val plainSecretCode = HashGenerator.generateHash(10).toByteArray(Charsets.UTF_8)
        val encodedSecret = Base32().encode(plainSecretCode)
        val totpUrl = GoogleAuthenticator(encodedSecret)
            .otpAuthUriBuilder()
            .label(user.username, null)
            .issuer(config.issuer)
            .buildToString()
        val recoverWords = getRandomWords()
        val acknowledge = userRepository.saveTOTPEncodedSecret(user, encodedSecret, recoverWords).acknowledge
        if (acknowledge) {
            return TOTPResponseBO(
                StringUtils.newStringUtf8(encodedSecret),
                totpUrl,
                recoverWords
            )
        }
        throw IllegalStateException()
    }

    suspend fun removeTOTP(user: UserBO): AcknowledgeBO = userRepository.removeTOTP(user)

}

suspend inline fun PipelineContext<Unit, ApplicationCall>.withAuthenticator(user: UserBO, block: () -> Unit) {
    val secret = user.totpEncodedSecret
    val code = call.request.header(TOTP_HEADER)
    val timeStamp = call.request.header(TIMESTAMP_HEADER)?.toLongOrNull()
    if (secret == null) {
        block()
        return
    }

    try {
        checkTOTP(secret, code, timeStamp) {
            block()
        }

    } catch (exc: Exception) {
        respondNotAuthorization()
    }
}

inline fun checkTOTP(
    secret: ByteArray,
    code: String?,
    timeStamp: Long?,
    block: () -> Unit = {}
) {
    val googleAuthenticator = GoogleAuthenticator(secret)

    code?.let { codeNotNull ->
        timeStamp?.let { timeStampNotNull ->
            if (googleAuthenticator.isValid(codeNotNull, Date(timeStampNotNull))) {
                block()

            }
            throw TotpNotFoundException

        } ?: throw TotpNotFoundException
    } ?: throw TotpNotFoundException
}

suspend fun PipelineContext<Unit, ApplicationCall>.respondNotAuthorization() {
    call.response.header(AUTHENTICATOR_HEADER, GOOGLE_AUTHENTICATOR)
    call.respond(HttpStatusCode.Unauthorized, TOTPRequestDTO(GOOGLE_AUTHENTICATOR, System.currentTimeMillis()))
}