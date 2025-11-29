package es.wokis.data.mapper.radio

import es.wokis.data.bo.radio.CountryCodeBO
import es.wokis.data.bo.radio.RadioBO
import es.wokis.data.bo.radio.RadioPageBO
import es.wokis.data.dbo.radio.CountryCodesDBO
import es.wokis.data.dbo.radio.RadioDBO
import es.wokis.data.dbo.radio.RadioPageDBO
import es.wokis.data.dto.radio.CountryCodeDTO
import es.wokis.data.dto.radio.RadioDTO
import es.wokis.data.dto.radio.RadioPageDTO

@JvmName("RadioDTOToBO")
fun List<RadioDTO>.toBO() = this.map { it.toBO() }

fun RadioDTO.toBO() = RadioBO(
    radioName = radioName,
    url = url,
    thumbnailUrl = thumbnailUrl,
    countryCode = countryCode
)

@JvmName("RadioBOToDTO")
fun List<RadioBO>.toDTO() = this.map { it.toDTO() }

fun RadioBO.toDTO() = RadioDTO(
    radioName = radioName,
    url = url,
    thumbnailUrl = thumbnailUrl,
    countryCode = countryCode
)

@JvmName("RadioBOToDBO")
fun List<RadioBO>.toDBO() = this.map { it.toDBO() }

fun RadioBO.toDBO() = RadioDBO(
    radioName = radioName,
    url = url,
    thumbnailUrl = thumbnailUrl,
    countryCode = countryCode
)

@JvmName("RadioDBOToBO")
fun List<RadioDBO>.toBO() = this.map { it.toBO() }

fun RadioDBO.toBO() = RadioBO(
    radioName = radioName,
    url = url,
    thumbnailUrl = thumbnailUrl,
    countryCode = countryCode
)

fun RadioPageDBO.toBO() = RadioPageBO(
    currentPage = currentPage,
    maxPage = maxPage,
    radios = radios.toBO()
)

fun RadioPageBO.toDTO() = RadioPageDTO(
    currentPage = currentPage,
    maxPage = maxPage,
    radios = radios.toDTO()
)

@JvmName("CountryCodeDBOToBO")
fun List<CountryCodesDBO>.toBO() = CountryCodeBO(
    countryCodes = this.map { it.countryCode }
)

@JvmName("CountryCodeBOToDTO")
fun List<CountryCodeBO>.toDTO() = this.map { it.toDTO() }

fun CountryCodeBO.toDTO() = CountryCodeDTO(
    countryCodes = countryCodes
)
