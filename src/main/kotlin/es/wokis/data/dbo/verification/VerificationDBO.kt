package es.wokis.data.dbo.verification

import es.wokis.data.dbo.user.UserDBO
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class VerificationDBO(
    @BsonId
    val id: Id<VerificationDBO>? = null,
    val email: String,
    val verificationToken: String,
    val timeStamp: Long,
)