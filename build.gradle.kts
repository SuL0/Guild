plugins {
    kotlin("jvm") version "1.5.21" apply true
    `maven-publish` apply true
    id("kr.entree.spigradle") version "2.2.3" apply false
}

group = "kr.sul.guild"
version = "1.0-SNAPSHOT"

val versionMirror = version
val rootName = "Guild"
val pluginStorage = "C:/MC-Development/PluginStorage"
val bukkitCopyDestination = "C:/MC-Development/마인즈서버/plugins"
val bungeeCopyDestination = "C:/MC-Development/Waterfall/plugins"

val getPluginName: (Project) -> String = { givenProject ->
    "$rootName-${givenProject.name}_S-${givenProject.version}.jar"
}
subprojects {
    apply(plugin="org.jetbrains.kotlin.jvm")
    apply(plugin="org.gradle.maven-publish")
    ext {
        set("rootName", "Guild")
        set("version", versionMirror)
        set("pluginStorage", pluginStorage)
    }


    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    }

    publishing {
        java {
            withSourcesJar()
            withJavadocJar()
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group as String
                version = rootProject.version as String
                from(components["java"])
            }
        }
    }

    tasks {
        compileJava.get().options.encoding = "UTF-8"
        compileKotlin.get().kotlinOptions.jvmTarget = "1.8"
        compileTestKotlin.get().kotlinOptions.jvmTarget = "1.8"

        jar {
            if (project.name == "Common") return@jar
            archiveFileName.set(getPluginName.invoke(project))
            val destination = when (project.name) {
                "Bungee" -> bungeeCopyDestination
                "Bukkit" -> bukkitCopyDestination
                else -> throw Exception()
            }
            destinationDirectory.set(file(destination))
            from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
            finalizedBy(publishToMavenLocal)
        }
    }
}

project("Bukkit").run {
    apply(plugin="kr.entree.spigradle")
}

