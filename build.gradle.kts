plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.shadow)
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

//    modules("key-binding-api-v1", "lifecycle-events-v1")
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
//
//yamlang {
//    targetSourceSets.set(mutableListOf(sourceSets["main"]))
//    inputDir.set("assets/${mod.id}/lang")
//}

java {
    withSourcesJar()
}

//plugins {
//    `maven-publish`
//    kotlin("jvm") version "1.9.23"
//    id("dev.kikugie.yamlang") version "1.4.+"
//    id("fabric-loom") version "1.6-SNAPSHOT"
//    id("me.modmuss50.mod-publish-plugin") version "0.4.+"
//    id("com.github.johnrengelman.shadow") version "8.1.1"
//}
//
//class ModData {
//    val id = property("mod.id").toString()
//    val name = property("mod.name").toString()
//    val version = property("mod.version").toString()
//    val group = property("mod.group").toString()
//}
//
//val mod = ModData()
//val mcVersion = property("deps.mc").toString()
//val mcDep = property("mod.mc_dep").toString()
//
//version = mod.version
//group = mod.group
//base { archivesName.set(mod.id) }
//
//repositories {
//    mavenCentral()
//    fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
//        forRepository { maven(url) }
//        filter { groups.forEach(::includeGroup) }
//    }
//    strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
//    strictMaven("https://maven.maxhenkel.de/releases", "de.maxhenkel.voicechat")
//    strictMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
//    strictMaven("https://maven.wispforest.io", "io.wispforest")
//    strictMaven("https://maven.lavalink.dev/releases", "dev.arbjerg")
//    strictMaven("https://jitpack.io/", "com.github.walkyst", "com.github.walkyst.JAADec-fork")
//    strictMaven("https://repo.plasmoverse.com/snapshots", "su.plo.voice", "su.plo.voice.api", "su.plo.slib")
//    strictMaven("https://repo.plasmoverse.com/releases", "su.plo.config")
//}
//
//dependencies {
//    fun modules(vararg modules: String) {
//        modules.forEach { modImplementation(fabricApi.module("fabric-$it", "${property("deps.fabric_api")}")) }
//    }
//
//    minecraft("com.mojang:minecraft:${mcVersion}")
//    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
//    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
//    modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.flk")}+kotlin.1.9.23")
//    modImplementation("io.wispforest:owo-lib:${property("deps.owo_lib")}")
//    include("io.wispforest:owo-sentinel:${property("deps.owo_lib")}")
//    modules("key-binding-api-v1", "lifecycle-events-v1")
//
//    compileOnly("de.maxhenkel.voicechat:voicechat-api:${property("deps.vc_api")}")
//    modRuntimeOnly("maven.modrinth:simple-voice-chat:${property("deps.simple_vc")}")
//
//    compileOnly("su.plo.voice.api:client:${property("deps.plasmo_api")}")
//    compileOnly("su.plo.config:config:1.0.0")
////    modRuntimeOnly("maven.modrinth:plasmo-voice:${property("deps.plasmo_vc")}")
//
//    // Testing
//    modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
////    modLocalRuntime("me.djtheredstoner:DevAuth-fabric:${property("test.devauth")}")
//    vineflowerDecompilerClasspath("org.vineflower:vineflower:1.10.0")
//    shadow(implementation("dev.arbjerg:lavaplayer:2.1.1") {
//        exclude("org.slf4j")
//    })
//}
//
//loom {
//    accessWidenerPath = rootProject.file("src/main/resources/soundboard.accesswidener")
//
//    decompilers {
//        get("vineflower").apply {
//            options.put("mark-corresponding-synthetics", "1")
//        }
//    }
//}
//
//yamlang {
//    targetSourceSets.set(mutableListOf(sourceSets["main"]))
//    inputDir.set("assets/${mod.id}/lang")
//}
//
//java {
//    withSourcesJar()
//}
//
//tasks.processResources {
//    inputs.property("id", mod.id)
//    inputs.property("name", mod.name)
//    inputs.property("version", mod.version)
//    inputs.property("mcdep", mcDep)
//
//    val map = mapOf(
//        "id" to mod.id,
//        "name" to mod.name,
//        "version" to mod.version,
//        "mcdep" to mcDep
//    )
//
//    filesMatching("fabric.mod.json") { expand(map) }
//}
//
//afterEvaluate {
//    loom {
//        runs {
//            configureEach {
//                vmArgs("-Xmx2G", "-XX:+UseShenandoahGC")
//
////                property("mixin.debug", "true")
////                property("mixin.debug.export.decompile", "false")
////                property("mixin.debug.verbose", "true")
//                property("mixin.dumpTargetOnFailure", "true")
//                // makes silent failures into hard-failures
////                property("mixin.checks", "true")
////                property("mixin.hotSwap", "true")
//
//                val mixinJarFile = configurations.compileClasspath.get().files {
//                    it.group == "net.fabricmc" && it.name == "sponge-mixin"
//                }.firstOrNull()
//                if (mixinJarFile != null)
//                    vmArg("-javaagent:$mixinJarFile")
//            }
//        }
//    }
//}
//
//publishMods {
//    file = tasks.remapJar.get().archiveFile
//    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
//    displayName = "${mod.name} ${mod.version}"
//    version = mod.version
//    changelog = rootProject.file("CHANGELOG.md").readText()
//    type = STABLE
//    modLoaders.add("fabric")
//
//    dryRun = providers.environmentVariable("MODRINTH_TOKEN")
//        .getOrNull() == null || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null
//
//    modrinth {
//        projectId = property("publish.modrinth").toString()
//        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
//        minecraftVersions.add(mcVersion)
//            requires {slug = "fabric-api"}
//            requires {slug = "fabric-language-kotlin"}
//            requires {slug = "simple-voice-chat"}
//            requires {slug = "owo-lib"}
//    }
//
//    curseforge {
//        projectId = property("publish.curseforge").toString()
//        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
//        minecraftVersions.add(mcVersion)
//            requires {slug = "fabric-api"}
//            requires {slug = "fabric-language-kotlin"}
//            requires {slug = "simple-voice-chat"}
//            requires {slug = "owo-lib"}
//
//    }
//}