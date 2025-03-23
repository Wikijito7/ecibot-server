package es.wokis.plugins

import es.wokis.di.localDataSourceModule
import es.wokis.di.remoteDataSourceModule
import es.wokis.di.repositoryModule
import es.wokis.di.serviceModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            listOf(
                repositoryModule,
                serviceModule,
                localDataSourceModule,
                remoteDataSourceModule
            )
        )
    }
}