package es.wokis.data.datasource.local.radio

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.dbo.radio.RadioCollectionDBO
import es.wokis.data.dbo.radio.RadioDBO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

interface RadioLocalDataSource {
    suspend fun areRadiosOutdated(): Boolean
    suspend fun saveRadios(radios: List<RadioDBO>, outdatedTimeStamp: Long)
    suspend fun getAllRadios(): List<RadioDBO>
    suspend fun areThereAnyRadioInserted(): Boolean
}

class RadioLocalDataSourceImpl(
    private val radioCollection: MongoCollection<RadioCollectionDBO>
) : RadioLocalDataSource {

    override suspend fun areRadiosOutdated(): Boolean =
        (radioCollection.find().firstOrNull()?.timestamp ?: 0) < System.currentTimeMillis()

    override suspend fun saveRadios(radios: List<RadioDBO>, outdatedTimeStamp: Long) {
        if (areThereAnyRadioInserted().not()) {
            radioCollection.insertOne(RadioCollectionDBO(radios = radios, timestamp = outdatedTimeStamp))
        } else {
            radioCollection.find().first().copy(radios = radios, timestamp = outdatedTimeStamp).let {
                val filter = Filters.eq(RadioCollectionDBO::id.name, it.id)
                radioCollection.replaceOne(filter, it)
            }
        }
    }

    override suspend fun getAllRadios(): List<RadioDBO> = radioCollection.find().firstOrNull()?.radios.orEmpty()

    override suspend fun areThereAnyRadioInserted(): Boolean = radioCollection.countDocuments() > 0L
}
