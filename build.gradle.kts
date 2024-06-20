plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.yamlang)
}

class ModData {
    val id: String by project
    val name: String by project
    val group: String by project
    val version: String by project
}
val mod = ModData()

allprojects {
    repositories {
        fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
            forRepository { maven(url) }
            filter { groups.forEach(::includeGroup) }
        }
        maven("https://jitpack.io")
        strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
        strictMaven("https://maven.wispforest.io", "io.wispforest")
        strictMaven("https://maven.lavalink.dev/releases", "dev.arbjerg")
        strictMaven("https://maven.maxhenkel.de/releases", "de.maxhenkel.voicechat")
        strictMaven("https://repo.plasmoverse.com/snapshots", "su.plo.voice", "su.plo.voice.api", "su.plo.slib")
        strictMaven("https://repo.plasmoverse.com/releases", "su.plo.config")
    }
}

dependencies {
    fun modules(vararg modules: String) {
        modules.forEach { modApi(fabricApi.module("fabric-$it", libs.versions.fabric.api.get())) }
    }
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)

    modApi(libs.owo) {
        exclude(group = "net.fabricmc")
    }
    include(libs.owo.sentinel)
    modApi(libs.fabric.api)
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/soundboard.accesswidener")

    runConfigs.configureEach {
        ideConfigGenerated(false)
        vmArgs("-Dmixin.debug.export=true", "-Dfabric.log.level=debug")
    }

    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

tasks.processResources {
    inputs.property("version", mod.version)
    inputs.property("minecraft", property("mcdep").toString())
    inputs.property("flk", property("flk").toString())
    inputs.property("owolib", property("owolib").toString())

    val map = mapOf(
        "version" to mod.version,
        "minecraft" to property("mcdep").toString(),
        "flk" to property("flk").toString(),
        "owolib" to property("owolib").toString(),
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
}

java {
    withSourcesJar()
}