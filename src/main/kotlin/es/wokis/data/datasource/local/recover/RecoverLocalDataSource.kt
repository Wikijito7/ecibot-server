package es.wokis.data.datasource.local.recover

import com.mongodb.client.MongoCollection
import es.wokis.data.bo.recover.RecoverBO
import es.wokis.data.dbo.recover.RecoverDBO
import es.wokis.data.mapper.recover.toBO
import es.wokis.data.mapper.recover.toDBO
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.id.toId
import org.litote.kmongo.regex

interface RecoverLocalDataSource {
    suspend fun getRecoverByToken(token: String): RecoverBO?
    suspend fun saveRecoverRequest(recover: RecoverBO): Boolean
    suspend fun removeRecover(id: String): Boolean
}

class RecoverLocalDataSourceImpl(private val recoverCollection: MongoCollection<RecoverDBO>) :
    RecoverLocalDataSource {
    override suspend fun getRecoverByToken(token: String): RecoverBO? =
        recoverCollection.findOne(RecoverDBO::recoverToken.regex(token))?.toBO()

    override suspend fun saveRecoverRequest(recover: RecoverBO): Boolean {
        return try {
            recoverCollection.insertOne(recover.toDBO()).wasAcknowledged()

        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }

    override suspend fun removeRecover(id: String): Boolean = try {
        val bsonId: Id<RecoverDBO> = ObjectId(id).toId()
        recoverCollection.deleteOne(RecoverDBO::id eq bsonId).wasAcknowledged()

    } catch (e: Throwable) {
        println(e.stackTraceToString())
        false
    }

}