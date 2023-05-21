package es.wokis.data.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import es.wokis.data.dbo.recover.RecoverDBO
import es.wokis.data.dbo.stat.FullStatDBO
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.dbo.verification.VerificationDBO
import es.wokis.plugins.config
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class AppDataBase {
    private val username = config.getString("db.user")
    private val password = config.getString("db.password")
    private val dataBaseUrl =
        "$MONGODB_PREFIX$username:$password@${config.getString("db.ip")}:${config.getString("db.port")}"
    private val databaseName = config.getString("db.databaseName")
    private val client = KMongo.createClient(
        MongoClientSettings.builder()
            .credential(MongoCredential.createCredential(username, databaseName, password.toCharArray()))
            .applyConnectionString(ConnectionString(dataBaseUrl)).build()
    )

    val database by lazy { client.getDatabase(databaseName) }
    val usersCollection by lazy { database.getCollection<UserDBO>("users") }
    val verificationCollection by lazy { database.getCollection<VerificationDBO>("verification") }
    val recoverCollection by lazy { database.getCollection<RecoverDBO>("recover") }
    val statsCollection by lazy { database.getCollection<FullStatDBO>("stats") }

    companion object {
        private const val MONGODB_PREFIX = "mongodb://"
    }
}