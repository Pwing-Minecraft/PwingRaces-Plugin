plugins {
    id("java")
    id("maven-publish")
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

description = "PwingRaces"

dependencies {
    api(project(":PwingRaces-API"))

    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("org.bstats:bstats-bukkit:1.4")

    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.4")
    compileOnly("com.github.MilkBowl:Vault:1.7.3") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.9")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.9.2")
    compileOnly("LibsDisguises:LibsDisguises:10.0.31")
    compileOnly("com.github.TheComputerGeek2:MagicSpells:3.5-Release")
    compileOnly("io.lumine:Mythic-Dist:5.2.1")
    compileOnly("me.blackvein.quests:quests-core:4.8.4")
    compileOnly("com.github.Archy-X:AureliumSkills:Beta1.3.20")

    compileOnly(":LoreAttributesRecoded")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

tasks {
    jar {
        archiveClassifier.set("unshaded")
    }

    shadowJar {
        relocate("org.bstats.bukkit", "net.pwing.races.metrics")

        archiveClassifier.set("")
        archiveFileName.set("PwingRaces.jar")
    }

    runServer {
        dependsOn(shadowJar)

        minecraftVersion("1.20.6")

        // Set Java 21 (1.20.6 requires Java 21)
        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    processResources {
        expand("version" to project.version)
    }
}
