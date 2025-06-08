package es.wokis.data.datasource.local.radio

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.dbo.radio.RadioCollectionDBO
import es.wokis.data.dbo.radio.RadioDBO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import java.util.regex.Pattern

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
                val filter = Filters.eq("_${RadioCollectionDBO::id.name}", it.id)
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

    override suspend fun getRadioByName(radioName: String): RadioDBO? = radioCollection
        .find<RadioCollectionDBO>()
        .projection(
            Projections.elemMatch(
                /* fieldName = */ RadioCollectionDBO::radios.name,
                /* filter = */ Filters.eq(RadioDBO::radioName.name, radioName)
            )
        )
        .firstOrNull()
        ?.radios
        ?.firstOrNull()

    override suspend fun findRadiosByName(prompt: String): List<RadioDBO> = radioCollection
        .aggregate<RadioDBO>(
            listOf(
                Aggregates.unwind("\$${RadioCollectionDBO::radios.name}"),
                Aggregates.match(
                    Filters.regex(
                        /* fieldName = */ "${RadioCollectionDBO::radios.name}.${RadioDBO::radioName.name}",
                        /* pattern = */ Pattern.compile(".*${Regex.escape(prompt)}.*", Pattern.CASE_INSENSITIVE)
                    )
                ),
                Aggregates.limit(MAX_DOCUMENTS_LIMIT),
                Aggregates.replaceRoot("\$${RadioCollectionDBO::radios.name}"),
            )
        )
        .toList()

    override suspend fun getNumberOfPagesAvailable(): Int =
        (radioCollection.find().firstOrNull()?.radios?.size ?: 0) / PAGINATION_MAX_DOCUMENTS_LIMIT
}
