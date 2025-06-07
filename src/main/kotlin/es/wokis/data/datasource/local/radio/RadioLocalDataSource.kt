package es.wokis.data.datasource.local.radio

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.dbo.radio.RadioCollectionDBO
import es.wokis.data.dbo.radio.RadioDBO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

interface RadioLocalDataSource {
    suspend fun areRadiosOutdated(): Boolean
    suspend fun saveRadios(radios: List<RadioDBO>, outdatedTimeStamp: Long)
    suspend fun getAllRadios(): List<RadioDBO>
    suspend fun getAllRadiosPaginated(page: Int): List<RadioDBO>
    suspend fun areThereAnyRadioInserted(): Boolean
    suspend fun getRadioByName(radioName: String): RadioDBO?
    suspend fun findRadiosByName(prompt: String): List<RadioDBO>
    suspend fun getNumberOfPagesAvailable(): Int
}

private const val MAX_DOCUMENTS_LIMIT = 30
private const val PAGINATION_MAX_DOCUMENTS_LIMIT = 102

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

    override suspend fun getAllRadiosPaginated(page: Int): List<RadioDBO> = radioCollection
        .find()
        .firstOrNull()
        ?.radios
        ?.subList(PAGINATION_MAX_DOCUMENTS_LIMIT * (page - 1), PAGINATION_MAX_DOCUMENTS_LIMIT * page)
        .orEmpty()

    override suspend fun areThereAnyRadioInserted(): Boolean = radioCollection.countDocuments() > 0L

    override suspend fun getRadioByName(radioName: String): RadioDBO? {
        val filter = Filters.eq(RadioDBO::radioName.name, radioName)
        return radioCollection.find<RadioDBO>(filter = filter).firstOrNull()
    }

    override suspend fun findRadiosByName(prompt: String): List<RadioDBO> {
        val filter = Filters.eq(RadioDBO::radioName.name, prompt)
        return radioCollection.find<RadioDBO>(filter = filter).limit(MAX_DOCUMENTS_LIMIT).toList()
    }

    override suspend fun getNumberOfPagesAvailable(): Int =
        (radioCollection.find().firstOrNull()?.radios?.size ?: 0) / PAGINATION_MAX_DOCUMENTS_LIMIT
}
