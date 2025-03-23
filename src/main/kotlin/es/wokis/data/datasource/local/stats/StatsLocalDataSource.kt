package es.wokis.data.datasource.local.stats

import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.bo.StatBO
import es.wokis.data.bo.StatsBO
import es.wokis.data.bo.StatsType
import es.wokis.data.dbo.stat.FullStatDBO
import es.wokis.utils.isTrue
import es.wokis.utils.takeAtMost
import kotlinx.coroutines.flow.toList

interface StatsLocalDataSource {
    suspend fun getSoundsStats(): StatsBO
    suspend fun getCommandsStats(): StatsBO
    suspend fun getUsersStats(): StatsBO

    suspend fun getKiwiStats(): StatsBO
}

private const val KIWI = "kiwi"

class StatsLocalDataSourceImpl(
    private val statsCollection: MongoCollection<FullStatDBO>
) : StatsLocalDataSource {

    override suspend fun getSoundsStats(): StatsBO =
        statsCollection
            .find()
            .toList()
            .asSequence()
            .filter { it.commandName.lowercase() != KIWI }
            .filter { it.soundName?.isNotEmpty().isTrue() }
            .groupBy { it.soundName }
            .map {
                StatBO(it.key, it.value.count())
            }
            .sortedByDescending { it.quantity }
            .toList()
            .takeAtMost(MAX_STATS)
            .let {
                StatsBO(StatsType.SOUND_STAT, it)
            }

    override suspend fun getCommandsStats(): StatsBO =
        statsCollection
            .find()
            .toList()
            .asSequence()
            .filter { it.commandName.lowercase() != KIWI }
            .filter { it.commandName.isNotEmpty() }
            .groupBy { it.commandName }
            .map {
                StatBO(it.key, it.value.count())
            }
            .sortedByDescending { it.quantity }
            .toList()
            .takeAtMost(MAX_STATS)
            .let {
                StatsBO(StatsType.COMMAND_STAT, it)
            }

    override suspend fun getUsersStats(): StatsBO =
        statsCollection
            .find()
            .toList()
            .asSequence()
            .filter { it.commandName.lowercase() != KIWI }
            .filter { it.username.isNotEmpty() }
            .groupBy { it.username }
            .map {
                StatBO(it.key, it.value.count())
            }
            .sortedByDescending { it.quantity }
            .toList()
            .takeAtMost(MAX_STATS)
            .let {
                StatsBO(StatsType.USER_STAT, it)
            }

    override suspend fun getKiwiStats(): StatsBO =
        statsCollection
            .find()
            .toList()
            .filter { it.username == KIWI && it.soundName?.isNotEmpty().isTrue() }
            .groupBy { it.soundName }
            .map {
                StatBO(it.key, it.value.count())
            }
            .sortedByDescending { it.quantity }
            .takeAtMost(MAX_STATS)
            .let {
                StatsBO(StatsType.KIWI_STAT, it)
            }

    companion object {
        private const val MAX_STATS = 5
    }

}