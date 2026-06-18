package es.wokis.data.datasource.local.sound

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.bo.sound.SoundBO
import es.wokis.data.dbo.SoundDBO
import es.wokis.data.mapper.sound.toBO
import es.wokis.data.mapper.sound.toDBO
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory

interface SoundsLocalDataSource {
    suspend fun getAllSounds(page: Int, limit: Int, status: String? = null): List<SoundBO>
    suspend fun getSoundsCount(status: String? = null): Long
    suspend fun getSoundByDisplayId(displayId: String): SoundBO?
    suspend fun getSoundsByCreatedBy(userId: String, page: Int, limit: Int): List<SoundBO>
    suspend fun getSoundsByCreatedByCount(userId: String): Long
    suspend fun createSound(sound: SoundBO): Boolean
    suspend fun updateSound(sound: SoundBO): Boolean
    suspend fun deleteSound(displayId: String): Boolean
}

class SoundsLocalDataSourceImpl(
    private val soundsCollection: MongoCollection<SoundDBO>
) : SoundsLocalDataSource {
    private val logger = LoggerFactory.getLogger(SoundsLocalDataSourceImpl::class.java)

    override suspend fun getAllSounds(page: Int, limit: Int, status: String?): List<SoundBO> {
        val skip = (page - 1) * limit
        val filter = status?.let { Filters.eq(SoundDBO::status.name, it) } ?: Filters.empty()
        return soundsCollection.find(filter)
            .sort(Sorts.descending(SoundDBO::createdOn.name))
            .skip(skip)
            .limit(limit)
            .toList()
            .map { it.toBO() }
    }

    override suspend fun getSoundsCount(status: String?): Long {
        val filter = status?.let { Filters.eq(SoundDBO::status.name, it) } ?: Filters.empty()
        return soundsCollection.countDocuments(filter)
    }

    override suspend fun getSoundByDisplayId(displayId: String): SoundBO? {
        val filter = Filters.eq(SoundDBO::displayId.name, displayId)
        return soundsCollection.find(filter).firstOrNull()?.toBO()
    }

    override suspend fun getSoundsByCreatedBy(userId: String, page: Int, limit: Int): List<SoundBO> {
        val skip = (page - 1) * limit
        val filter = Filters.eq(SoundDBO::createdBy.name, userId)
        return soundsCollection.find(filter)
            .sort(Sorts.descending(SoundDBO::createdOn.name))
            .skip(skip)
            .limit(limit)
            .toList()
            .map { it.toBO() }
    }

    override suspend fun getSoundsByCreatedByCount(userId: String): Long {
        val filter = Filters.eq(SoundDBO::createdBy.name, userId)
        return soundsCollection.countDocuments(filter)
    }

    override suspend fun createSound(sound: SoundBO): Boolean = try {
        soundsCollection.insertOne(sound.toDBO()).wasAcknowledged()
    } catch (e: Throwable) {
        logger.error("Failed to create sound", e)
        false
    }

    override suspend fun updateSound(sound: SoundBO): Boolean {
        val filter = Filters.eq(SoundDBO::displayId.name, sound.displayId)
        return soundsCollection.replaceOne(filter, sound.toDBO()).wasAcknowledged()
    }

    override suspend fun deleteSound(displayId: String): Boolean = try {
        soundsCollection.deleteOne(Filters.eq(SoundDBO::displayId.name, displayId)).wasAcknowledged()
    } catch (e: Throwable) {
        logger.error("Failed to delete sound", e)
        false
    }
}
