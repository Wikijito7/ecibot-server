package es.wokis.data.datasource.local.radio

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Aggregates.project
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Projections.computed
import com.mongodb.client.model.Projections.include
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.dbo.radio.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.conversions.Bson
import java.util.regex.Pattern

interface RadioLocalDataSource {
    suspend fun areRadiosOutdated(): Boolean
    suspend fun saveRadios(radios: List<RadioDBO>, outdatedTimeStamp: Long)
    suspend fun getAllRadios(): List<RadioDBO>
    suspend fun getAllRadiosPaginated(page: Int): RadioPageDBO
    suspend fun areThereAnyRadioInserted(): Boolean
    suspend fun getRadioByName(radioName: String): RadioDBO?
    suspend fun findRadiosByName(prompt: String): List<RadioDBO>
    suspend fun getNumberOfPagesAvailable(): Int
}

private const val MAX_DOCUMENTS_LIMIT = 30
private const val PAGINATION_MAX_DOCUMENTS_LIMIT = 150

class RadioLocalDataSourceImpl(
    private val radioCollection: MongoCollection<RadioCollectionDBO>
) : RadioLocalDataSource {

    override suspend fun areRadiosOutdated(): Boolean {
        val projection: Bson = project(
            include(RadioCollectionDBO::timestamp.name)
        )
        val timestamp = radioCollection
            .aggregate<RadiosTimestampDBO>(listOf(projection))
            .toList()
            .firstOrNull()
            ?.timestamp
            ?: 0
        return timestamp < System.currentTimeMillis()
    }

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

    override suspend fun getAllRadiosPaginated(page: Int): RadioPageDBO = findRadios(
        prompt = "",
        batchSize = PAGINATION_MAX_DOCUMENTS_LIMIT,
        skip = PAGINATION_MAX_DOCUMENTS_LIMIT * (page - 1)
    ).let {
        RadioPageDBO(
            currentPage = page,
            maxPage = getNumberOfPagesAvailable(),
            radios = it
        )
    }

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

    override suspend fun findRadiosByName(prompt: String): List<RadioDBO> = findRadios(prompt)

    private suspend fun findRadios(prompt: String, batchSize: Int = MAX_DOCUMENTS_LIMIT, skip: Int = 0) = radioCollection
            .aggregate<RadioDBO>(
                listOf(
                    Aggregates.unwind("\$${RadioCollectionDBO::radios.name}"),
                    Aggregates.match(
                        Filters.regex(
                            /* fieldName = */ "${RadioCollectionDBO::radios.name}.${RadioDBO::radioName.name}",
                            /* pattern = */ Pattern.compile(".*${Regex.escape(prompt)}.*", Pattern.CASE_INSENSITIVE)
                        )
                    ),
                    Aggregates.limit(batchSize),
                    Aggregates.skip(skip),
                    Aggregates.replaceRoot("\$${RadioCollectionDBO::radios.name}"),
                )
            )
            .toList()

    override suspend fun getNumberOfPagesAvailable(): Int {
        val projection: Bson = project(
            computed("radiosCount", Document("\$size", "\$radios"))
        )
        val count = radioCollection
            .aggregate<RadiosCountDBO>(listOf(projection))
            .toList()
            .firstOrNull()
            ?.radiosCount
            ?: 0

        return count / PAGINATION_MAX_DOCUMENTS_LIMIT
    }
}
