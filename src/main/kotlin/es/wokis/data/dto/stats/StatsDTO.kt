package es.wokis.data.dto.stats

import es.wokis.data.bo.StatsType
import kotlinx.serialization.SerialName

data class StatsDTO(
    @SerialName("type")
    val type: StatsType,
    @SerialName("stats")
    val stats: List<StatDTO>
)

data class StatDTO(
    @SerialName("description")
    val description: String?,
    @SerialName("quantity")
    val quantity: Int
)
