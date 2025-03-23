package es.wokis.di

import com.mongodb.kotlin.client.coroutine.MongoCollection
import es.wokis.data.database.AppDataBase
import es.wokis.data.datasource.local.radio.RadioLocalDataSource
import es.wokis.data.datasource.local.radio.RadioLocalDataSourceImpl
import es.wokis.data.datasource.local.recover.RecoverLocalDataSource
import es.wokis.data.datasource.local.recover.RecoverLocalDataSourceImpl
import es.wokis.data.datasource.local.stats.StatsLocalDataSource
import es.wokis.data.datasource.local.stats.StatsLocalDataSourceImpl
import es.wokis.data.datasource.local.user.UserLocalDataSource
import es.wokis.data.datasource.local.user.UserLocalDataSourceImpl
import es.wokis.data.datasource.local.verify.VerifyLocalDataSource
import es.wokis.data.datasource.local.verify.VerifyLocalDataSourceImpl
import es.wokis.data.datasource.remote.radio.RadioRemoteDataSource
import es.wokis.data.datasource.remote.radio.RadioRemoteDataSourceImpl
import es.wokis.data.dbo.radio.RadioDBO
import es.wokis.data.dbo.recover.RecoverDBO
import es.wokis.data.dbo.stat.FullStatDBO
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.dbo.verification.VerificationDBO
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.text.Charsets

private const val SOCKET_TIMEOUT_MILLIS = 20000L
private const val CONNECT_TIMEOUT_MILLIS = 20000L
private const val REQUEST_TIMEOUT_MILLIS = 20000L

val localDataSourceModule = module {
    single { AppDataBase() }
    single(named("usersCollection")) { getUsersCollection(get()) as MongoCollection<UserDBO> }
    single(named("verificationCollection")) { getVerificationCollection(get()) as MongoCollection<VerificationDBO> }
    single(named("recoverCollection")) { getRecoverCollection(get()) as MongoCollection<RecoverDBO> }
    single(named("statsCollection")) { getStatsCollection(get()) as MongoCollection<FullStatDBO> }
    single(named("radioCollection")) { getRadioCollection(get()) as MongoCollection<RadioDBO> }
    single<UserLocalDataSource> { UserLocalDataSourceImpl(get(named("usersCollection"))) }
    single<VerifyLocalDataSource> { VerifyLocalDataSourceImpl(get(named("verificationCollection"))) }
    single<RecoverLocalDataSource> { RecoverLocalDataSourceImpl(get(named("recoverCollection"))) }
    single<StatsLocalDataSource> { StatsLocalDataSourceImpl(get(named("statsCollection"))) }
    single<RadioLocalDataSource> { RadioLocalDataSourceImpl(get(named("radioCollection"))) }
}

val remoteDataSourceModule = module {
    single<HttpClient> {
        HttpClient(CIO) {
            BrowserUserAgent()
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.BODY
            }
            install(ContentNegotiation) {
                json()
            }
            install(Resources)
            install(Auth)
            install(HttpTimeout) {
                socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
                connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
                requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            }
            Charsets {
                register(Charsets.UTF_8)
            }
            expectSuccess = true
        }
    }
    single<RadioRemoteDataSource> {
        RadioRemoteDataSourceImpl(
            httpClient = get()
        )
    }
}

private fun getUsersCollection(database: AppDataBase) = database.usersCollection

private fun getVerificationCollection(database: AppDataBase) = database.verificationCollection

private fun getRecoverCollection(database: AppDataBase) = database.recoverCollection

private fun getStatsCollection(database: AppDataBase) = database.statsCollection

private fun getRadioCollection(database: AppDataBase) = database.radioCollection