package es.wokis.data.repository.radio

import es.wokis.data.bo.radio.RadioBO
import es.wokis.data.datasource.local.radio.RadioLocalDataSource
import es.wokis.data.datasource.remote.radio.RadioRemoteDataSource
import es.wokis.data.dto.radio.RadioDTO

interface RadioRepository {
    suspend fun getAllRadios(): List<RadioDTO>
    suspend fun getRadioByName(radioName: String): RadioBO
    suspend fun findRadiosByPrompt(prompt: String): List<RadioBO>
    suspend fun getRadiosByCountry(countryCode: String): List<RadioBO>
}

class RadioRepositoryImpl(
    private val radioRemoteDataSource: RadioRemoteDataSource,
    private val radioLocalDataSource: RadioLocalDataSource
) : RadioRepository {

    override suspend fun getAllRadios(): List<RadioDTO> {
        return radioRemoteDataSource.fetchAllRadios()
    }

    override suspend fun getRadioByName(radioName: String): RadioBO {
        TODO("Not yet implemented")
    }

    override suspend fun findRadiosByPrompt(prompt: String): List<RadioBO> {
        TODO("Not yet implemented")
    }

    override suspend fun getRadiosByCountry(countryCode: String): List<RadioBO> {
        TODO("Not yet implemented")
    }
}
