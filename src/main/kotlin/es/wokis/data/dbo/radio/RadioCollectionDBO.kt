package es.wokis.data.dbo.radio

import org.bson.codecs.pojo.annotations.BsonId

data class RadioCollectionDBO(
    @BsonId
    val id: String? = null,
    val radios: List<RadioDBO>,
    val timestamp: Long
)