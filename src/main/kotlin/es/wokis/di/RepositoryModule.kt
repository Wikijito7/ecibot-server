package es.wokis.di

import es.wokis.data.repository.StatsRepository
import es.wokis.data.repository.StatsRepositoryImpl
import es.wokis.data.repository.radio.RadioRepository
import es.wokis.data.repository.radio.RadioRepositoryImpl
import es.wokis.data.repository.recover.RecoverRepository
import es.wokis.data.repository.recover.RecoverRepositoryImpl
import es.wokis.data.repository.sound.SoundRepository
import es.wokis.data.repository.sound.SoundRepositoryImpl
import es.wokis.data.repository.user.UserRepository
import es.wokis.data.repository.user.UserRepositoryImpl
import es.wokis.data.repository.verify.VerifyRepository
import es.wokis.data.repository.verify.VerifyRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<VerifyRepository> { VerifyRepositoryImpl(get(), get()) }
    single<RecoverRepository> { RecoverRepositoryImpl(get(), get(), get()) }
    single<StatsRepository> { StatsRepositoryImpl(get()) }
    single<SoundRepository> {
        SoundRepositoryImpl()
    }
    single<RadioRepository> {
        RadioRepositoryImpl(
            radioLocalDataSource = get(),
            radioRemoteDataSource = get()
        )
    }
}