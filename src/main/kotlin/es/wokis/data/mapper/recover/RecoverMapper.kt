package es.wokis.data.mapper.recover

import es.wokis.data.bo.recover.RecoverBO
import es.wokis.data.dbo.recover.RecoverDBO
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId
import java.util.*

fun RecoverBO.toDBO() = RecoverDBO(
    id = id?.let { ObjectId(it).toId() },
    email = email,
    recoverToken = verificationToken,
    timeStamp = timeStamp.time,
)

fun RecoverDBO.toBO() = RecoverBO(
    id = id.toString(),
    email = email,
    verificationToken = recoverToken,
    timeStamp = Date(timeStamp),
)