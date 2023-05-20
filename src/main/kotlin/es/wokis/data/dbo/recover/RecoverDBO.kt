package es.wokis.data.dbo.recover

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class RecoverDBO(
    @BsonId
    val id: Id<RecoverDBO>? = null,
    val email: String,
    val recoverToken: String,
    val timeStamp: Long,
)