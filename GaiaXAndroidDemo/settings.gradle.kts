rootProject.name = "GaiaXAndroidDemo"

include(":app")

include(":microbenchmark")

include(":GaiaXAndroid")
project(":GaiaXAndroid").projectDir = file("../GaiaXAndroid")

include(":GaiaXAndroidJS")
project(":GaiaXAndroidJS").projectDir = file("../GaiaXAndroidJS")

include(":GaiaX-Android-QuickJS")
project(":GaiaX-Android-QuickJS").projectDir = file("../GaiaX-Android-QuickJS")

include(":GaiaXAndroidClientToStudio")
project(":GaiaXAndroidClientToStudio").projectDir = file("../GaiaXAndroidClientToStudio")

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