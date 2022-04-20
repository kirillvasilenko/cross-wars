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

    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:_")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:_")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:_")

    implementation(Ktor.client.core)
    implementation("io.ktor:ktor-client-js:_")

    implementation(Ktor.client.logging)

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