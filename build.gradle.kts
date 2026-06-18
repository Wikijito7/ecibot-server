plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    jacoco
    alias(libs.plugins.sonarqube)
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
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.junit.jupiter)
}

val shadowJarTask = tasks.named("shadowJar")
tasks.named("distZip") { dependsOn(shadowJarTask) }
tasks.named("distTar") { dependsOn(shadowJarTask) }
tasks.named("startScripts") { dependsOn(shadowJarTask) }
tasks.named("startShadowScripts") { dependsOn(tasks.named("jar")) }

ktor {
    fatJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required = true
        xml.outputLocation.set(file("build/reports/jacoco/test-results/jacocoTestReport.xml"))
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

sonar {
    properties {
        val projectKey = System.getenv("SONAR_PROJECT_KEY")
        val organization = System.getenv("SONAR_ORGANIZATION")
        val exclusions = listOf(
            "**/*BO.kt",
            "**/*DTO.kt",
            "**/*Exception.kt",
            "src/main/kotlin/es/wokis/Application.kt",
            "*.kts",
            "**/di/*.kt",
        )
        property("sonar.projectKey", projectKey)
        property("sonar.organization", organization)
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.exclusions", exclusions)
    }
}
