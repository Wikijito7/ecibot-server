package es.wokis.di

import com.mongodb.client.MongoCollection
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
import org.koin.core.qualifier.named
import org.koin.dsl.module

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