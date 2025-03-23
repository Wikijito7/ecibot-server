package es.wokis.data.datasource.local.user

import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.bo.user.UserBO
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.mapper.user.toBO
import es.wokis.data.mapper.user.toDBO
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import java.util.regex.Pattern

interface UserLocalDataSource {
    suspend fun getAllUsers(): List<UserBO>
    suspend fun getUserById(id: String): UserBO?
    suspend fun getUserByEmail(email: String): UserBO?
    suspend fun getUserByUsername(username: String): UserBO?
    suspend fun getUserByUsernameOrEmail(username: String, email: String = EMPTY_TEXT): UserBO?
    suspend fun createUser(user: UserBO): Boolean
    suspend fun updateUser(user: UserBO): Boolean
}

class UserLocalDataSourceImpl(private val userCollection: MongoCollection<UserDBO>) : UserLocalDataSource {
    private val getUsernameCaseInsensitive: (element: String) -> Pattern = {
        Pattern.compile("\\b$it\\b", Pattern.CASE_INSENSITIVE)
    }

    override suspend fun getAllUsers(): List<UserBO> = userCollection.find().toList().map {
        it.toBO()
    }.toList()

    override suspend fun getUserById(id: String): UserBO? {
        val bsonId: Id<UserDBO> = ObjectId(id).toId()
        return userCollection.findOne(UserDBO::id eq bsonId)?.toBO()
    }

    override suspend fun getUserByEmail(email: String): UserBO? =
        userCollection.findOne(UserDBO::email.regex(email))?.toBO()

    override suspend fun getUserByUsername(username: String): UserBO? =
        userCollection.findOne(UserDBO::username.regex(getUsernameCaseInsensitive(username)))?.toBO()

    override suspend fun getUserByUsernameOrEmail(username: String, email: String): UserBO? =
        userCollection.findOne(
            or(
                UserDBO::username.regex(getUsernameCaseInsensitive(username)),
                UserDBO::email.regex(email.takeIf { it.isNotBlank() } ?: username)
            )
        )?.toBO()

    override suspend fun createUser(user: UserBO): Boolean {
        return try {
            userCollection.insertOne(user.toDBO()).wasAcknowledged()

        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }

    override suspend fun updateUser(user: UserBO): Boolean {
        val bsonId: Id<UserDBO> = ObjectId(user.id).toId()
        return userCollection.updateOne(UserDBO::id eq bsonId, user.toDBO()).wasAcknowledged()
    }
}