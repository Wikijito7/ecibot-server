package es.wokis.data.dbo.user

import es.wokis.data.constants.ServerConstants
import es.wokis.data.constants.ServerConstants.DEFAULT_LANG
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import org.bson.types.ObjectId
import java.util.Date

data class UserDBO(
    @SerialName("_id")
    @Contextual val id: ObjectId? = null,
    val username: String,
    val email: String,
    val password: String,
    val lang: String = DEFAULT_LANG,
    val image: String = ServerConstants.EMPTY_TEXT,
    val createdOn: Long = Date().time,
    val emailVerified: Boolean = false,
    val totpEncodedSecret: ByteArray? = null,
    val sessions: List<String> = emptyList(),
    val recoverWords: List<String> = emptyList()
)
