package es.wokis.data.dto.stats

import es.wokis.data.bo.StatsType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatsDTO(
    @SerialName("type")
    val type: StatsType,
    @SerialName("stats")
    val stats: List<StatDTO>
)

@Serializable
data class StatDTO(
    @SerialName("description")
    val description: String?,
    @SerialName("quantity")
    val quantity: Int
)
