package es.wokis.data.mapper.recover

import es.wokis.data.bo.recover.RecoverBO
import es.wokis.data.dbo.recover.RecoverDBO
import org.bson.types.ObjectId
import java.util.*

fun RecoverBO.toDBO() = RecoverDBO(
    id = id?.let { ObjectId(it) },
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
