package es.wokis.data.dbo.verification

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import org.bson.types.ObjectId

data class VerificationDBO(
    @SerialName("_id")
    @Contextual val id: ObjectId? = null,
    val email: String,
    val verificationToken: String,
    val timeStamp: Long,
)
