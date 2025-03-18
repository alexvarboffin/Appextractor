import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.kotlin.compose)

    //alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

fun versionCodeDate(): Int {
    return SimpleDateFormat("yyMMdd").format(Date()).toInt()
}

//ext {
//    dd = "dd"
//}
//buildscript {
//    repositories {
//        //mavenCentral() // or
//         jcenter()
//    }
//
//    dependencies {
//        classpath "com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.2"
//    }
//}
//apply plugin: "com.android.application"
// make sure this line comes *after* you apply the Android plugin
//apply plugin: "com.getkeepsafe.dexcount"

android {
    namespace = "com.walhalla.appextractor"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTools.get()

    val versionPropsFile = file("version.properties")
    if (versionPropsFile.canRead()) {
        val code = versionCodeDate()

        defaultConfig {
            multiDexEnabled = true
            resConfigs("ru", "en", "zh-rCN", "de", "vi", "ar", "pt", "es")
            vectorDrawables.useSupportLibrary = true
            applicationId = "com.walhalla.appextractor"
            minSdk = libs.versions.minSdk.get().toInt()
            targetSdk = libs.versions.targetSdk.get().toInt()
            versionCode = code
            versionName = "1.4.$code"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            setProperty("archivesBaseName", "ApkChief")
        }
    } else {
        throw GradleException("Could not read version.properties!")
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
        getByName("debug") {
            signingConfig = signingConfigs.getByName("x")
            versionNameSuffix = "-DEMO"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("x")
            versionNameSuffix = ".release"
        }
    }


    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

//    flavorDimensions += "W"
//
//    productFlavors {
//        create("pro") {
//            dimension = "W"
//        }
//    }

    lint {
        abortOnError = false
        disable.add("InvalidPackage")
    }
}
//configurations {
//    compile.exclude group: "org.apache.httpcomponents", module: "httpclient"
//    //compile.exclude group: "com.android.support"
//}

//repositories {
//    flatDir {
//        dirs "libs"
//    }
//}
dependencies {
    //implementation fileTree(dir: "libs", include: ["*.jar", "*.aar"])
    //implementation fileTree(dir: "libs", include: ["*.jar"])
    //implementation(name:"toasty-production-release", ext:"aar")
    //implementation(name:"threader-release", ext:"aar")

    implementation (files("libs/toasty-production-release.aar"))
//    implementation (files("libs/threader-release.aar"))

    //implementation fileTree(dir: "libs", include: ["*.jar"])

    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.preference.ktx)
    //    implementation("com.google.firebase:firebase-ads:$FOO"){
    //        exclude group: "com.android.support"
    //    }

    //    testImplementation "junit:junit:4.12"
    //    androidTestImplementation "com.android.support.test:runner:1.0.2"
    //    androidTestImplementation "com.android.support.test.espresso:espresso-core:3.0.2"
    implementation(libs.dropbox.core.sdk)
    //    implementation("com.google.android.gms:play-services-drive:15.0.0") {
    //        exclude group: "org.apache.httpcomponents"
    //    }
    //Google drive include

//google    implementation("com.google.android.gms:play-services-auth:18.1.0")
//google    implementation("pub.devrel:easypermissions:3.0.0")
//google
//google    implementation("com.google.http-client:google-http-client-gson:1.36.0")
//google    implementation("com.google.api-client:google-api-client-android:1.30.10") {
//google        exclude group: "org.apache.httpcomponents"
//google    }

//"com.google.apis:google-api-services-drive:16.0.0"
//    implementation("com.google.android.gms:play-services-drive:17.0.0")) {
//        exclude group: "org.apache.httpcomponents"
//    }

    //google implementation ("com.google.apis:google-api-services-drive:v3-rev99-1.23.0"
    //google ) {
    //google     exclude group: "org.apache.httpcomponents"
    //google }

    //noinspection GradleDependency
    implementation(libs.okhttp)

    //releaseImplementation "com.android.support:multidex:1.0.3"
    implementation(libs.androidx.multidex)

    //implementation("com.walhalla.threader:threader-android-app")

    //implementation(project(":wads"))

    implementation(project(":features:ui"))
    implementation(project(":extractor"))
    implementation(project(":shared"))


    //implementation(project(":threader"))

    implementation(libs.androidx.core.ktx)

    //implementation("com.squareup.picasso:picasso:2.71828")

    //Firebase Crashlytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    implementation(libs.play.services.ads)
    //implementation("com.github.pokercc:ExpandableRecyclerView:0.8.1")
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    //implementation("org.jetbrains.kotlin:kotlin-stdlib:$rootProject.kotlin_version")
    //implementation@@@@@@@@@@@@@@@core:1.10.3"
    implementation(libs.flexbox)
    implementation(libs.glide)

//    implementation("com.facebook.react:hermes-engine:+")
//    //implementation("com.facebook.react:libjsi:+")
//    implementation("com.facebook.soloader:soloader:0.10.4")
//    implementation("com.facebook.react:react-native:+")
    implementation(libs.kotlin.stdlib.jdk8)

    //implementation("com.github.iqiyi:Neptune:2.7.0")
    implementation("org.qiyi.video:neptune:2.7.0")
}

//apply plugin: "com.getkeepsafe.dexcount"
//dexcount {
//    format = "list"
//    includeClasses = true
//    includeFieldCount = false
//    orderByMethodCount = true
//}