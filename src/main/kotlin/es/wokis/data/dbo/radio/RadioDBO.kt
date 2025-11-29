package es.wokis.data.dbo.radio

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class RadioDBO(
    @BsonId
    val id: ObjectId? = null,
    val radioName: String,
    val url: String,
    val thumbnailUrl: String,
    val countryCode: String
)
