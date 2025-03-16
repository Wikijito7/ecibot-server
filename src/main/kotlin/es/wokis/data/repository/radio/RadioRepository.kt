package es.wokis.data.repository.radio

import es.wokis.data.bo.radio.RadioBO
import es.wokis.data.datasource.local.radio.RadioLocalDataSource
import es.wokis.data.datasource.remote.radio.RadioRemoteDataSource

interface RadioRepository {
    fun getAllRadios(): List<RadioBO>
    fun getRadioByName(radioName: String): RadioBO
    fun findRadiosByPrompt(prompt: String): List<RadioBO>
    fun getRadiosByCountry(countryCode: String): List<RadioBO>
}

class RadioRepositoryImpl(
    private val radioRemoteDataSource: RadioRemoteDataSource,
    private val radioLocalDataSource: RadioLocalDataSource
) : RadioRepository {

    override fun getAllRadios(): List<RadioBO> {
        TODO("Not yet implemented")
    }

    override fun getRadioByName(radioName: String): RadioBO {
        TODO("Not yet implemented")
    }

    override fun findRadiosByPrompt(prompt: String): List<RadioBO> {
        TODO("Not yet implemented")
    }

    override fun getRadiosByCountry(countryCode: String): List<RadioBO> {
        TODO("Not yet implemented")
    }
}
