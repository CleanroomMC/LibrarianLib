import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val modVersion: String by ext.properties
val modName: String by ext.properties
val modGroup: String by ext.properties
val corePlugin: String by ext.properties

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version("2.0.0")
    id("com.gtnewhorizons.retrofuturagradle") version("1.3.35")
}

minecraft {
    mcVersion.set("1.12.2")
    extraRunJvmArguments.add("-Dfml.coremods.load=${corePlugin}")
}

version = modVersion
description = "A library for the TeamWizardry mods, continuously maintained by CleanroomMC."
base.archivesBaseName = modName + "-" + minecraft.mcVersion.get()

sourceSets["main"].allSource.srcDir("src/example/java")
sourceSets["main"].allSource.srcDir("src/api/java")
sourceSets["main"].resources.srcDir("src/example/resources")

repositories {
    maven {
        name = "CleanroomMC"
        url = uri("https://maven.cleanroommc.com")
    }
}

dependencies {
    api("io.github.chaosunity.forgelin:Forgelin-Continuous:2.0.0.0") {
        isTransitive = false
    }
    runtimeOnly("io.github.chaosunity.forgelin:Forgelin-Continuous:2.0.0.0") {
        isTransitive = false
    }
}

tasks {
    getByName<Jar>("jar") {
        manifest {
            attributes(
                "FMLCorePluginContainsFMLMod" to true,
                "FMLCorePlugin" to corePlugin,
            )
        }
    }

    getByName<ProcessResources>("processResources") {
        val props = mapOf(
                "version" to project.version,
                "mcversion" to minecraft.mcVersion
        )

        inputs.properties(props)

        from(sourceSets["main"].resources.srcDirs) {
            include("mcmod.info")
            expand(props)
            duplicatesStrategy = DuplicatesStrategy.WARN
        }

        from(sourceSets["main"].resources.srcDirs) {
            exclude("mcmod.info")
            duplicatesStrategy = DuplicatesStrategy.WARN
        }
    }

    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
            javaParameters.set(true)
            languageVersion.set(KotlinVersion.KOTLIN_1_9)
            freeCompilerArgs.add("-Xjvm-default=all-compatibility")
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        targetCompatibility = "1.8"
        sourceCompatibility = "1.8"
    }
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = modGroup
            artifactId = modName
            version = modVersion
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "CleanroomMavenLibrarianLib"
            url = uri("https://repo.cleanroommc.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
