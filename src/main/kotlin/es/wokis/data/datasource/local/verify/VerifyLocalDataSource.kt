package es.wokis.data.datasource.local.verify

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.bo.verification.VerificationBO
import es.wokis.data.dbo.verification.VerificationDBO
import es.wokis.data.mapper.verify.toBO
import es.wokis.data.mapper.verify.toDBO
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

interface VerifyLocalDataSource {
    suspend fun getVerificationByToken(token: String): VerificationBO?
    suspend fun addVerification(verification: VerificationBO): Boolean
    suspend fun removeVerification(id: String): Boolean
}

class VerifyLocalDataSourceImpl(private val verificationCollection: MongoCollection<VerificationDBO>) :
    VerifyLocalDataSource {
    override suspend fun getVerificationByToken(token: String): VerificationBO? =
        verificationCollection.find(Filters.regex(VerificationDBO::verificationToken.name, token)).firstOrNull()?.toBO()

    override suspend fun addVerification(verification: VerificationBO): Boolean {
        return try {
            verificationCollection.insertOne(verification.toDBO()).wasAcknowledged()

        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }

    override suspend fun removeVerification(id: String): Boolean = try {
        verificationCollection.deleteOne(Filters.eq(VerificationDBO::id.name, ObjectId(id))).wasAcknowledged()

    } catch (e: Throwable) {
        println(e.stackTraceToString())
        false
    }
}
