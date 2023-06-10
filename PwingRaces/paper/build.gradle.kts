plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.0.1"

    // Applied for run paper task
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "net.pwing.races"
version = "1.5.1-SNAPSHOT"

dependencies {
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")

    api(project(":PwingRaces-API"))
    implementation(project(":core"))
}

tasks.processResources {
    expand("version" to project.version)
}

tasks.shadowJar {
    relocate("org.bstats.bukkit", "net.pwing.races.metrics")
}