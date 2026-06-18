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

interface SoundsLocalDataSource {
    suspend fun getAllSounds(page: Int, limit: Int): List<SoundBO>
    suspend fun getSoundsCount(): Long
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

    override suspend fun getAllSounds(page: Int, limit: Int): List<SoundBO> {
        val skip = (page - 1) * limit
        return soundsCollection.find()
            .sort(Sorts.descending(SoundDBO::createdOn.name))
            .skip(skip)
            .limit(limit)
            .toList()
            .map { it.toBO() }
    }

    override suspend fun getSoundsCount(): Long =
        soundsCollection.countDocuments()

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

    override suspend fun createSound(sound: SoundBO): Boolean {
        return try {
            soundsCollection.insertOne(sound.toDBO()).wasAcknowledged()
        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }

    override suspend fun updateSound(sound: SoundBO): Boolean {
        val filter = Filters.eq(SoundDBO::displayId.name, sound.displayId)
        return soundsCollection.replaceOne(filter, sound.toDBO()).wasAcknowledged()
    }

    override suspend fun deleteSound(displayId: String): Boolean {
        val filter = Filters.eq(SoundDBO::displayId.name, displayId)
        return try {
            soundsCollection.deleteOne(filter).wasAcknowledged()
        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }
}
