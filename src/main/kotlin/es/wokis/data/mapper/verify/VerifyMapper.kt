package es.wokis.data.mapper.verify

import es.wokis.data.bo.verification.VerificationBO
import es.wokis.data.dbo.verification.VerificationDBO
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId
import java.util.*

fun VerificationBO.toDBO() = VerificationDBO(
    id = id?.let { ObjectId(it).toId() },
    email = email,
    verificationToken = verificationToken,
    timeStamp = timeStamp.time,
)

fun VerificationDBO.toBO() = VerificationBO(
    id = id.toString(),
    email = email,
    verificationToken = verificationToken,
    timeStamp = Date(timeStamp),
)