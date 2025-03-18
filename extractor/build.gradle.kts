plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.walhalla.extractor"
    compileSdk = 35

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
                "\"220535441:AAGSE2J0uJp0X87cxyup4kL9ytybvb78AGk\""
            )
            buildConfigField("String", "KEY_TELEGRAM_CHAT_ID", "\"-1001857544330\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            buildConfigField("String", "KEY_TELEGRAM_TOKEN", "\"\"")
            buildConfigField("String", "KEY_TELEGRAM_CHAT_ID", "\"\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.dropbox.core.sdk)
    implementation(libs.okhttp)
    implementation(project(":shared"))
    implementation("org.qiyi.video:neptune:2.7.0")
}