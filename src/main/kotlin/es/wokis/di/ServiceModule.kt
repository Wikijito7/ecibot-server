package es.wokis.di

import es.wokis.services.EmailService
import es.wokis.services.TOTPService
import org.koin.dsl.module

val serviceModule = module {
    single { TOTPService(get()) }
    single { EmailService(get()) }
}