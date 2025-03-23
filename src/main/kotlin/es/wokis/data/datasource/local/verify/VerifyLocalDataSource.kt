package es.wokis.data.datasource.local.verify

import com.mongodb.client.MongoCollection
import es.wokis.data.bo.verification.VerificationBO
import es.wokis.data.dbo.verification.VerificationDBO
import es.wokis.data.mapper.verify.toBO
import es.wokis.data.mapper.verify.toDBO
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.id.toId
import org.litote.kmongo.regex

interface VerifyLocalDataSource {
    suspend fun getVerificationByToken(token: String): VerificationBO?
    suspend fun addVerification(verification: VerificationBO): Boolean
    suspend fun removeVerification(id: String): Boolean
}

class VerifyLocalDataSourceImpl(private val verificationCollection: MongoCollection<VerificationDBO>) :
    VerifyLocalDataSource {
    override suspend fun getVerificationByToken(token: String): VerificationBO? =
        verificationCollection.findOne(VerificationDBO::verificationToken.regex(token))?.toBO()

    override suspend fun addVerification(verification: VerificationBO): Boolean {
        return try {
            verificationCollection.insertOne(verification.toDBO()).wasAcknowledged()

        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }

    override suspend fun removeVerification(id: String): Boolean = try {
        val bsonId: Id<VerificationDBO> = ObjectId(id).toId()
        verificationCollection.deleteOne(VerificationDBO::id eq bsonId).wasAcknowledged()

    } catch (e: Throwable) {
        println(e.stackTraceToString())
        false
    }

}