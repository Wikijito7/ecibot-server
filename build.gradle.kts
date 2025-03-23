plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.plugin.serialization)
    alias(libs.plugins.ktor.plugin)
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
    // Ktor Server
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.metrics)
    implementation(libs.ktor.server.metrics.micrometer)

    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization.kotlin.json)
    implementation(libs.ktor.client.client.resources)
    implementation(libs.ktor.client.client.auth)

    // Prometheus
    implementation(libs.micrometer.registry.prometheus)

    // Mongodb
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
    testImplementation(libs.ktor.client.mock)
}

ktor {
    fatJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
    }
}