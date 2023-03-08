rootProject.name = "GaiaXAndroidDemo"

include(":app")

include(":microbenchmark")

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