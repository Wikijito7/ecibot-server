package es.wokis.data.dto.stats

import com.google.gson.annotations.SerializedName
import es.wokis.data.bo.StatsType

data class StatsDTO(
    @SerializedName("type")
    val type: StatsType,
    @SerializedName("stats")
    val stats: List<StatDTO>
)

data class StatDTO(
    @SerializedName("description")
    val description: String?,
    @SerializedName("quantity")
    val quantity: Int
)
