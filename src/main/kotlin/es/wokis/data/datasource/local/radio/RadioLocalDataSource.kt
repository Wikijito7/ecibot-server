package es.wokis.data.datasource.local.radio

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Projections.computed
import com.mongodb.client.model.Projections.include
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.dbo.radio.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
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
    suspend fun findRadiosByPromptPaginated(prompt: String, page: Int): RadioPageDBO
    suspend fun getNumberOfStationPagesAvailable(name: String = ""): Int
    suspend fun findRadiosByCountryCode(countryCode: String): List<RadioDBO>
    suspend fun findRadiosByCountryCodePaginated(countryCode: String): List<RadioDBO>
    suspend fun getCountryCodes(countryCode: String): List<CountryCodeDBO>

}

private const val MAX_DOCUMENTS_LIMIT = 30
private const val PAGINATION_MAX_DOCUMENTS_LIMIT = 150

class RadioLocalDataSourceImpl(
    private val radioCollection: MongoCollection<RadioCollectionDBO>
) : RadioLocalDataSource {

    override suspend fun areRadiosOutdated(): Boolean {
        val projection: Bson = Aggregates.project(
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
            maxPage = getNumberOfStationPagesAvailable(),
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

    override suspend fun findRadiosByPromptPaginated(prompt: String, page: Int): RadioPageDBO = findRadios(
        prompt = prompt,
        batchSize = PAGINATION_MAX_DOCUMENTS_LIMIT,
        skip = PAGINATION_MAX_DOCUMENTS_LIMIT * (page - 1)
    ).let {
        RadioPageDBO(
            currentPage = page,
            maxPage = getNumberOfStationPagesAvailable(prompt),
            radios = it
        )
    }

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
                    Aggregates.skip(skip),
                    Aggregates.limit(batchSize),
                    Aggregates.replaceRoot("\$${RadioCollectionDBO::radios.name}"),
                )
            )
            .toList()

    override suspend fun getNumberOfStationPagesAvailable(name: String): Int {
        val count = radioCollection
            .aggregate<RadiosCountDBO>(
                listOf(
                    Aggregates.unwind("\$${RadioCollectionDBO::radios.name}"),
                    Aggregates.match(
                        Filters.regex(
                            /* fieldName = */ "${RadioCollectionDBO::radios.name}.${RadioDBO::radioName.name}",
                            /* pattern = */ Pattern.compile(".*${Regex.escape(name)}.*", Pattern.CASE_INSENSITIVE)
                        )
                    ),
                    Aggregates.count(RadioDBO::radioName.name),
                    Aggregates.project(
                        computed(RadiosCountDBO::radiosCount.name, "\$${RadioDBO::radioName.name}")
                    )
                )
            )
            .toList()
            .firstOrNull()
            ?.radiosCount
            ?: 0

        return (count / PAGINATION_MAX_DOCUMENTS_LIMIT) + 1
    }

    override suspend fun findRadiosByCountryCode(countryCode: String): List<RadioDBO> {
        TODO("Not yet implemented")
    }

    override suspend fun findRadiosByCountryCodePaginated(countryCode: String): List<RadioDBO> {
        TODO("Not yet implemented")
    }

    override suspend fun getCountryCodes(countryCode: String): List<CountryCodeDBO> = radioCollection
        .aggregate<CountryCodeDBO>(
            listOf(
                Aggregates.unwind("\$${RadioCollectionDBO::radios.name}"),
                Aggregates.count(RadioDBO::countryCode.name),
                Aggregates.project(
                    computed(CountryCodeDBO::countryCodes.name, "\$${RadioDBO::countryCode.name}")
                )
            )
        )
        .toList()
}
