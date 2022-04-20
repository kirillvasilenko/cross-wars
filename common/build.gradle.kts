plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
}

group = "com.vkir"
version = "1.0-SNAPSHOT"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Kotlin.stdlib.common)

                implementation(KotlinX.serialization.core)
                implementation(KotlinX.serialization.json)

                implementation(Ktor.client.core)
                implementation(Ktor.client.logging)
                implementation("io.ktor:ktor-client-content-negotiation:_")
                implementation("io.ktor:ktor-serialization-kotlinx-json:_")

                api(Koin.core)
                api(KotlinX.datetime)

                api("io.github.microutils:kotlin-logging:_")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
    }
}