plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 33
    namespace = "com.alibaba.gaiax.demo"

    defaultConfig {
        applicationId = "com.alibaba.gaiax.demo"
        versionCode = 1
        versionName = "1.0"
        minSdk = 23 // Minimum supported version for macrobenchmark
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
            abiFilters.add("x86")
            abiFilters.add("x86_64")
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    sourceSets {
        this.getByName("main") {
            java.srcDir("src/main/java")
            java.srcDir("src/main/kotlin")
            jniLibs.srcDir("src/main/jniLibs")
        }
        this.getByName("androidTest") {
            java.srcDir("src/androidTest/java")
            java.srcDir("src/androidTest/kotlin")
            res.srcDirs("src/androidTest/res")
            jniLibs.srcDir("src/main/jniLibs")
        }
    }

    buildTypes {
        val release = getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }

    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)

    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.compose.activity)
    implementation(libs.compose.material)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.constraintlayout)
    implementation(libs.core)
    implementation(libs.datastore)
    implementation(libs.google.material)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.stdlib)
    implementation(libs.lifecycle)
    implementation(libs.profile.installer)
    implementation(libs.squareup.curtains)
    implementation(libs.tracing)
    implementation(libs.viewmodel)
    androidTestImplementation(libs.benchmark.junit)
}

dependencies {
    implementation(libs.gaiax)
    implementation(libs.gaiaxadapter)
    implementation(libs.gaiaxanalyze)
    implementation(libs.gaiaxclienttostudio)

    implementation("com.airbnb.android:lottie:4.1.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.alibaba:fastjson:1.2.79")
    implementation("com.github.jenly1314:zxing-lite:2.1.1")
}
