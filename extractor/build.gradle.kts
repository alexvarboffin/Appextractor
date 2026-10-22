plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

fun quoteBuildConfig(value: String?): String {
    val escaped = (value ?: "").replace("\\", "\\\\").replace("\"", "\\\"")
    return "\"$escaped\""
}

android {
    namespace = "com.walhalla.extractor"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }


    buildTypes {
        getByName("debug") {
            buildConfigField(
                "String",
                "KEY_TELEGRAM_TOKEN",
                quoteBuildConfig(findProperty("TELEGRAM_TOKEN_DEBUG")?.toString())
            )
            buildConfigField(
                "String",
                "KEY_TELEGRAM_CHAT_ID",
                quoteBuildConfig(findProperty("TELEGRAM_CHAT_ID_DEBUG")?.toString())
            )
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "KEY_TELEGRAM_TOKEN",
                quoteBuildConfig(findProperty("TELEGRAM_TOKEN_RELEASE")?.toString())
            )
            buildConfigField(
                "String",
                "KEY_TELEGRAM_CHAT_ID",
                quoteBuildConfig(findProperty("TELEGRAM_CHAT_ID_RELEASE")?.toString())
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures{
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    //implementation(libs.androidx.room.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.dropbox.core.sdk)
    implementation(libs.okhttp)
    implementation(project(":shared"))
    // Google Play policy: Device and Network Abuse (iQIYI Neptune downloads executable code)
    //implementation(libs.neptune)
}