pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        maven { url = uri("https://www.jitpack.io") }
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    //repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        maven { url = uri("https://www.jitpack.io") }
        gradlePluginPortal()
//        maven("https://jitpack.io")

    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


include(":app")
rootProject.name = "Appextractor"

//include(":threader")
//project(":threader").projectDir = File("D:\\walhalla\\sdk\\android\\multithreader\\threader\\")

//include(":wads")
//project(":wads").projectDir = File("D:\\walhalla\\sdk\\android\\UI\\wads")

include(":features:ui")
project(":features:ui").projectDir = File("../WalhallaUI\\features\\ui")

include(":shared")
project(":shared").projectDir = File("../WalhallaUI\\shared")

//include(":SdkLibrary")
//project(":SdkLibrary").projectDir = File("D:\\Nonesorted\\Neptune\\SdkLibrary")

include(":extractor")
include(":compose")
