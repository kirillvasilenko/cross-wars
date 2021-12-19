group = "com.vkir"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        classpath("org.jetbrains.kotlin:kotlin-serialization:_")
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}