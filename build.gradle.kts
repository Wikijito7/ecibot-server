plugins {
    application
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.1.1"
}

group = "es.wokis"
version = "1.0"

application {
    mainClass.set("es.wokis.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.metrics)
    implementation(libs.ktor.server.metrics.micrometer)

    // Prometheus
    implementation(libs.micrometer.registry.prometheus)

    // Mongodb
    implementation(libs.kmongo) // deprecated
    implementation(libs.mongobd)

    // Koin
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Bcrypt
    implementation(libs.jbcrypt)

    // JavaMail
    implementation(libs.javax.mail.api)
    implementation(libs.javax.mail)

    // TOTP
    implementation(libs.kotlin.onetimepassword)

    // Commons codec
    implementation(libs.commons.codec)

    // Logs
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)

    // Tests
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}

ktor {
    fatJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
    }
}