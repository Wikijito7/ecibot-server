package es.wokis.data.datasource.remote.radio

import es.wokis.data.dto.radio.RadioDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface RadioRemoteDataSource {
    suspend fun fetchAllRadios(): List<RadioDTO>
}

private const val RADIO_BROWSER_ALL_RADIOS = /*"http://all.api.radio-browser.info/json/stations"*/ "https://wokis.es/radios/radios.json"

class RadioRemoteDataSourceImpl(
    private val httpClient: HttpClient
) : RadioRemoteDataSource {

    override suspend fun fetchAllRadios(): List<RadioDTO> {
        val radioList: List<RadioDTO> = httpClient.get(RADIO_BROWSER_ALL_RADIOS) {
            headers {
                userAgent("ECIBotKt/1.0")
                accept(ContentType.Application.Json)
            }
        }.body()
        return radioList
    }
}
