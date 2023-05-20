package es.wokis.data.bo.user

import es.wokis.data.constants.ServerConstants.DEFAULT_LANG
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import io.ktor.server.auth.*
import java.util.*

data class UserBO(
    val id: String? = null,
    val username: String,
    val email: String,
    val password: String,
    val image: String = EMPTY_TEXT,
    val lang: String = DEFAULT_LANG,
    val createdOn: Long = Date().time,
    val totpEncodedSecret: ByteArray? = null,
    val currentSession: String? = null,
    val emailVerified: Boolean = false,
    val sessions: List<String> = emptyList(),
    val recoverWords: List<String> = emptyList()
) : Principal {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserBO

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (image != other.image) return false
        if (lang != other.lang) return false
        if (createdOn != other.createdOn) return false
        if (totpEncodedSecret != null) {
            if (other.totpEncodedSecret == null) return false
            if (!totpEncodedSecret.contentEquals(other.totpEncodedSecret)) return false
        } else if (other.totpEncodedSecret != null) return false
        if (emailVerified != other.emailVerified) return false
        if (sessions != other.sessions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + lang.hashCode()
        result = 31 * result + createdOn.hashCode()
        result = 31 * result + (totpEncodedSecret?.contentHashCode() ?: 0)
        result = 31 * result + emailVerified.hashCode()
        result = 31 * result + sessions.hashCode()
        return result
    }
}
