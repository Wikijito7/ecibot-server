package es.wokis.data.mapper.radio

import es.wokis.data.bo.radio.RadioBO
import es.wokis.data.dto.radio.RadioDTO

@JvmName("RadioDTOToBO")
fun List<RadioDTO>.toBO() = this.map { it.toBO() }

fun RadioDTO.toBO() = RadioBO(
    radioName = radioName,
    url = url,
    thumbnailUrl = thumbnailUrl,
    countryCode = countryCode
)

// fun List<RadioBO>