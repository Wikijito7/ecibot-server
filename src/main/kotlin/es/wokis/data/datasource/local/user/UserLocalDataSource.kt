package es.wokis.data.datasource.local.user

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.bo.user.UserBO
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.mapper.user.toBO
import es.wokis.data.mapper.user.toDBO
import kotlinx.coroutines.flow.firstOrNull
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
        val filter = Filters.eq(UserDBO::id.name, ObjectId(id))
        return userCollection.find(filter).firstOrNull()?.toBO()
    }

    override suspend fun getUserByEmail(email: String): UserBO? {
        val filter = Filters.regex(UserDBO::email.name, email)
        return userCollection.find(filter).firstOrNull()?.toBO()
    }

    override suspend fun getUserByUsername(username: String): UserBO? {
        val filter = Filters.regex(UserDBO::username.name, getUsernameCaseInsensitive(username))
        return userCollection.find(filter).firstOrNull()?.toBO()
    }

    override suspend fun getUserByUsernameOrEmail(username: String, email: String): UserBO? =
        userCollection.find(
            Filters.or(
                Filters.regex(UserDBO::username.name, getUsernameCaseInsensitive(username)),
                Filters.regex(UserDBO::email.name, email.takeIf { it.isNotBlank() } ?: username)
            )
        ).firstOrNull()?.toBO()

    override suspend fun createUser(user: UserBO): Boolean {
        return try {
            userCollection.insertOne(user.toDBO()).wasAcknowledged()

        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }

    override suspend fun updateUser(user: UserBO): Boolean {
        val filter = Filters.eq(UserDBO::id.name, ObjectId(user.id))
        return userCollection.replaceOne(filter, user.toDBO()).wasAcknowledged()
    }
}
