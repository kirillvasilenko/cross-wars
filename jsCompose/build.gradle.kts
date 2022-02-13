plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.vkir"
version = "1.0-SNAPSHOT"

kotlin {
    js {
        browser {
            webpackTask {
                cssSupport.enabled = true
            }

            runTask {
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
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}