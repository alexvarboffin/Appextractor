import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
}

fun versionCodeDate(): Int {
    return SimpleDateFormat("yyMMdd").format(Date()).toInt()
}

android {
    // Kotlin/R package stays compose; install id matches legacy for in-place updates
    namespace = "com.walhalla.compose"
    // 36: needed by dokar3/sonner (TV toasts); targetSdk can stay lower
    compileSdk = 36

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.walhalla.appextractor"
        // sonner (TV toasts) requires 24+
        minSdk = 24
        targetSdk = 35
        val code = versionCodeDate()
        versionCode = code
        versionName = "1.4.$code"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        setProperty("archivesBaseName", "ApkChief")
    }

    signingConfigs {
        create("x") {
            keyAlias = "app-extractor"
            keyPassword = "@!sfuQ123zpc"
            storeFile = file("keystore/w.jks")
            storePassword = "@!sfuQ123zpc"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("x")
            versionNameSuffix = "-DEMO"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("x")
            versionNameSuffix = ".release"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // Adaptive nav (bar ↔ rail) for tablets / landscape — see adaptive skill Step 2
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.3.2")
    // Android TV Material3 (tv shell) — see compose-tv-from-mobile / FireTv
    implementation("androidx.tv:tv-material:1.0.1")
    // TV toasts (top-center) instead of android.widget.Toast
    // Prefer API-compatible release; force kotlin-stdlib to project Kotlin (sonner 0.4 pulls 2.4.x)
    implementation("io.github.dokar3:sonner:0.3.8")
    // QR encoder for LAN APK transfer (phone + TV)
    implementation("com.google.zxing:core:3.5.3")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(project(":extractor"))
    implementation(project(":shared"))

    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")


    //implementation("androidx.compose.material:material:1.7.8")
    implementation(libs.androidx.material.icons.extended.android)
    implementation("com.google.accompanist:accompanist-pager:0.30.1")

    val nav_version = "2.8.9"
    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
