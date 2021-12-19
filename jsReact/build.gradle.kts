plugins {
    id("org.jetbrains.kotlin.js")
}

group = "com.vkir"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.240-kotlin-1.5.30")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.240-kotlin-1.5.30")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:5.3.1-pre.240-kotlin-1.5.30")

    implementation(Ktor.client.core)
    implementation("io.ktor:ktor-client-js:_")

    implementation(Ktor.client.json)
    implementation(Ktor.client.logging)
    implementation(Ktor.client.serialization)

    implementation(KotlinX.serialization.core)

    implementation("io.github.microutils:kotlin-logging-js:_")

    testImplementation(kotlin("test"))

    implementation(project(":common"))
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()
    }
}