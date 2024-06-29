pluginManagement {
    repositories {
        maven {
            // RetroFuturaGradle
            name = "GTNH Maven"
            url = uri("https://nexus.gtnewhorizons.com/repository/public/")
            mavenContent {
                includeGroup("com.gtnewhorizons")
                includeGroup("com.gtnewhorizons.retrofuturagradle")
            }
        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

// enableFeaturePreview("STABLE_PUBLISHING")

val kotlin_version: String by settings
val dokka_version: String by settings
val forgegradle_version: String by settings
val bintray_version: String by settings
val artifactory_version: String by settings
val abc_version: String by settings

rootProject.name = "LibrarianLib"