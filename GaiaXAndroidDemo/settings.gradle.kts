rootProject.name = "GaiaXAndroidDemo"

include(":app")

include(":GaiaXAndroidMicrobenchmark")

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