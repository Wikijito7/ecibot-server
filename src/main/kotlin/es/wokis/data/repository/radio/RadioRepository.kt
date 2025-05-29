package es.wokis.data.repository.radio

import es.wokis.data.bo.radio.RadioBO
import es.wokis.data.datasource.local.radio.RadioLocalDataSource
import es.wokis.data.datasource.remote.radio.RadioRemoteDataSource
import es.wokis.data.dto.radio.RadioDTO
import es.wokis.data.mapper.radio.toBO
import es.wokis.data.mapper.radio.toDBO
import es.wokis.data.mapper.radio.toDTO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

private const val RADIO_OUTDATED_DELAY: Long = 24 * 60 * 60 * 1000L

interface RadioRepository {
    suspend fun getAllRadios(): List<RadioDTO>
    suspend fun getRadioByName(radioName: String): RadioBO
    suspend fun findRadiosByPrompt(prompt: String): List<RadioBO>
    suspend fun getRadiosByCountry(countryCode: String): List<RadioBO>
    suspend fun fetchAndSaveRemoteRadios()
}

class RadioRepositoryImpl(
    private val radioRemoteDataSource: RadioRemoteDataSource,
    private val radioLocalDataSource: RadioLocalDataSource
) : RadioRepository {

    override suspend fun getAllRadios(): List<RadioDTO> =
        radioLocalDataSource.getAllRadios().toBO().toDTO()

    override suspend fun getRadioByName(radioName: String): RadioBO {
        TODO("Not yet implemented")
    }

    override suspend fun findRadiosByPrompt(prompt: String): List<RadioBO> {
        TODO("Not yet implemented")
    }

    override suspend fun getRadiosByCountry(countryCode: String): List<RadioBO> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAndSaveRemoteRadios() {
        if (radioLocalDataSource.areRadiosOutdated().not()) {
            return
        }
        radioRemoteDataSource.fetchAllRadios().toBO().toDBO().let {
            radioLocalDataSource.saveRadios(it, System.currentTimeMillis() + RADIO_OUTDATED_DELAY)
        }
    }
}
