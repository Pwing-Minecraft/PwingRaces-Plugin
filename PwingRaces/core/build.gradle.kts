plugins {
    java
    `maven-publish`
}

description = "PwingRaces"

dependencies {
    compileOnlyApi(project(":PwingRaces-API"))

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
    compileOnly("me.blackvein.quests:quests-core:4.8.3")
    compileOnly("com.github.Archy-X:AureliumSkills:Beta1.3.20")

    compileOnly(":LoreAttributesRecoded")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

tasks.processResources {
    expand("version" to project.version)
}