package es.wokis.data.mapper.stats

import es.wokis.data.bo.StatBO
import es.wokis.data.bo.StatsBO
import es.wokis.data.dto.stats.StatDTO
import es.wokis.data.dto.stats.StatsDTO

fun List<StatsBO>.toDTO() = this.map {
    it.toDTO()
}

fun StatsBO.toDTO() = StatsDTO(
    type,
    stats.toDTO()
)

fun List<StatBO>.toDTO() = this.map {
    it.toDTO()
}

fun StatBO.toDTO() = StatDTO(
    description = description,
    quantity = quantity
)