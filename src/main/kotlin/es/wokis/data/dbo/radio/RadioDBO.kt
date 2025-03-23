package es.wokis.data.dbo.radio

import org.bson.codecs.pojo.annotations.BsonId

data class RadioDBO(
    @BsonId
    val id: String,
    val radioName: String,
    val url: String,
    val thumbnailUrl: String,
    val countryCode: String
)