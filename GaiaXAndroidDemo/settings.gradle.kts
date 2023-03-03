rootProject.name = "GaiaXAndroidDemo"

include(":app")

include(":GXAnalyzeAndroid")
project(":GXAnalyzeAndroid").projectDir = file("../GaiaXAnalyze/GXAnalyzeAndroid")

include(":GaiaXAndroid")
project(":GaiaXAndroid").projectDir = file("../GaiaXAndroid")

include(":GaiaXAndroidAdapter")
project(":GaiaXAndroidAdapter").projectDir = file("../GaiaXAndroidAdapter")

include(":GaiaXAndroidClientToStudio")
project(":GaiaXAndroidClientToStudio").projectDir = file("../GaiaXAndroidClientToStudio")

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