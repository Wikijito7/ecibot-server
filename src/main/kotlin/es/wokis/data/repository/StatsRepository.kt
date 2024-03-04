package es.wokis.data.repository

import es.wokis.data.bo.StatsBO
import es.wokis.data.datasource.stats.StatsLocalDataSource

interface StatsRepository {
    suspend fun getStats(): List<StatsBO>
}

class StatsRepositoryImpl(
    private val statsLocalDataSource: StatsLocalDataSource
) : StatsRepository {

    override suspend fun getStats(): List<StatsBO> {
        val soundsStats = statsLocalDataSource.getSoundsStats()
        val commandsStats = statsLocalDataSource.getCommandsStats()
        val usersStats = statsLocalDataSource.getUsersStats()
        val kiwiStats = statsLocalDataSource.getKiwiStats()
        return listOf(soundsStats, commandsStats, usersStats, kiwiStats)
    }

}
