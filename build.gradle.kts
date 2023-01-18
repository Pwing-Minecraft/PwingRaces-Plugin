import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

dependencies {
    implementation(project(":PwingRaces"))
    implementation(project(":PwingRaces-API"))
}

description = "PwingRaces-Parent"

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = "net.pwing.races"
    version = "1.5.0"

    java.sourceCompatibility = JavaVersion.VERSION_17
    java.targetCompatibility = JavaVersion.VERSION_17

    repositories {
        flatDir {
            dirs(rootDir.path + "/lib")
        }

        mavenCentral()
        mavenLocal()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.md-5.net/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")
        maven("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://nexus.hc.to/content/repositories/pub_releases/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://jitpack.io")
        maven("https://repo.dmulloy2.net/nexus/repository/public/")
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://mvn.lumine.io/repository/maven-public/")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

tasks.withType<ShadowJar> {
    from("src/main/java/resources") {
        include("*")
    }

    archiveFileName.set("PwingRaces.jar")
    relocate("org.bstats.bukkit", "net.pwing.races.metrics")
}