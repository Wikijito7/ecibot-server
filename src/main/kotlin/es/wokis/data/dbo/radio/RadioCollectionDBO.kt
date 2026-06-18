package es.wokis.data.dbo.radio

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

data class RadioCollectionDBO(
    @BsonId
    val id: ObjectId? = null,
    @BsonProperty("radios")
    val radios: List<RadioDBO>?,
    @BsonProperty("timestamp")
    val timestamp: Long?
)
