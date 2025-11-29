package es.wokis.data.datasource.local.recover

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.bo.recover.RecoverBO
import es.wokis.data.dbo.recover.RecoverDBO
import es.wokis.data.mapper.recover.toBO
import es.wokis.data.mapper.recover.toDBO
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

interface RecoverLocalDataSource {
    suspend fun getRecoverByToken(token: String): RecoverBO?
    suspend fun saveRecoverRequest(recover: RecoverBO): Boolean
    suspend fun removeRecover(id: String): Boolean
}

class RecoverLocalDataSourceImpl(private val recoverCollection: MongoCollection<RecoverDBO>) :
    RecoverLocalDataSource {

    override suspend fun getRecoverByToken(token: String): RecoverBO? {
        val filter = Filters.regex(RecoverDBO::recoverToken.name, token)
        return recoverCollection.find(filter).firstOrNull()?.toBO()
    }

    override suspend fun saveRecoverRequest(recover: RecoverBO): Boolean {
        return try {
            recoverCollection.insertOne(recover.toDBO()).wasAcknowledged()

        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }

    override suspend fun removeRecover(id: String): Boolean = try {
        val filter = Filters.eq(RecoverDBO::id.name, ObjectId(id))
        recoverCollection.deleteOne(filter).wasAcknowledged()

    } catch (e: Throwable) {
        println(e.stackTraceToString())
        false
    }

}