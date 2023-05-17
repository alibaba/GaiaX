rootProject.name = "GaiaXAndroidDemo"

include(":app")

include(":microbenchmark")

include(":GaiaXAndroid")
project(":GaiaXAndroid").projectDir = file("../GaiaXAndroid")

pluginManagement {
    repositories {
        mavenLocal()
        google()
        maven {
            url = uri("https://jitpack.io")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        maven {
            url = uri("https://jitpack.io")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}