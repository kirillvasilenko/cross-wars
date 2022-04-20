plugins {
    kotlin("jvm")
    application
    kotlin("plugin.serialization")
}

group = "com.vkir"
version = "1.0-SNAPSHOT"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xuse-experimental=kotlin.time.ExperimentalTime"
    )
}

dependencies {
    implementation(Ktor.server.core)
    implementation(Ktor.server.netty)

    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")

    implementation("io.ktor:ktor-server-auth:_")
    implementation("io.ktor:ktor-server-auth-jwt:_")

    implementation("io.ktor:ktor-server-websockets:_")
    implementation("io.ktor:ktor-server-call-logging:_")
    implementation("io.ktor:ktor-server-compression:_")

    implementation("io.insert-koin:koin-ktor:_")
    implementation("io.insert-koin:koin-logger-slf4j:_")
    implementation("io.github.microutils:kotlin-logging:_")
    implementation("ch.qos.logback:logback-classic:_")

    testImplementation("io.ktor:ktor-server-tests:_")

    implementation(project(":common"))
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}
