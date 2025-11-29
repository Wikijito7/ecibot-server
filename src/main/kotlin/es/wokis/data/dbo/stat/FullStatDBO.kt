package es.wokis.data.dbo.stat

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class FullStatDBO(
    @BsonId
    val id: ObjectId,
    val commandName: String,
    val soundName: String?,
    val username: String,
    val timestamp: Long
)
