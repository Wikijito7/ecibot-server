package es.wokis.data.datasource.stats

import com.mongodb.client.MongoCollection
import es.wokis.data.bo.StatBO
import es.wokis.data.bo.StatsBO
import es.wokis.data.bo.StatsType
import es.wokis.data.dbo.stat.FullStatDBO
import es.wokis.utils.takeAtMost

interface StatsLocalDataSource {
    suspend fun getSoundsStats(): StatsBO
    suspend fun getCommandsStats(): StatsBO
    suspend fun getUsersStats(): StatsBO
}

class StatsLocalDataSourceImpl(
    private val recoverCollection: MongoCollection<FullStatDBO>
) : StatsLocalDataSource {

    override suspend fun getSoundsStats(): StatsBO =
        recoverCollection
            .find()
            .groupBy { it.soundName }
            .map {
                StatBO(it.key, it.value.count())
            }
            .sortedByDescending { it.quantity }
            .takeAtMost(MAX_STATS)
            .let {
                StatsBO(StatsType.SOUND_STAT, it)
            }

    override suspend fun getCommandsStats(): StatsBO =
        recoverCollection
            .find()
            .groupBy { it.commandName }
            .map {
                StatBO(it.key, it.value.count())
            }
            .sortedByDescending { it.quantity }
            .takeAtMost(MAX_STATS)
            .let {
                StatsBO(StatsType.COMMAND_STAT, it)
            }

    override suspend fun getUsersStats(): StatsBO =
        recoverCollection
            .find()
            .groupBy { it.username }
            .map {
                StatBO(it.key, it.value.count())
            }
            .sortedByDescending { it.quantity }
            .takeAtMost(MAX_STATS)
            .let {
                StatsBO(StatsType.USER_STAT, it)
            }

    companion object {
        private const val MAX_STATS = 5
    }

}