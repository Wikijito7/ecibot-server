package es.wokis.data.dbo.stat

import org.bson.codecs.pojo.annotations.BsonId

data class FullStatDBO(
    @BsonId
    val id: String,
    val commandName: String,
    val soundName: String?,
    val username: String
)
