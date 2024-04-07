plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.23"
    id("dev.kikugie.yamlang") version "1.4.+"
    id("fabric-loom") version "1.6-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.4.+"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

val mod = ModData()
val mcVersion = property("deps.mc").toString()
val mcDep = property("mod.mc_dep").toString()

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set(mod.id) }

repositories {
    mavenCentral()
    fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
    strictMaven("https://maven.maxhenkel.de/releases", "de.maxhenkel.voicechat")
    strictMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    strictMaven("https://maven.wispforest.io", "io.wispforest")
}

dependencies {
    fun modules(vararg modules: String) {
        modules.forEach { modImplementation(fabricApi.module("fabric-$it", "${property("deps.fabric_api")}")) }
    }

    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.flk")}+kotlin.1.9.23")
    modImplementation("de.maxhenkel.voicechat:voicechat-api:${property("deps.vc_api")}")
    shadow(implementation("com.googlecode.soundlibs:mp3spi:${property("deps.mp3spi")}") {
        exclude(group = "junit", module = "junit")
    })
    modImplementation("io.wispforest:owo-lib:${property("deps.owo_lib")}")
    include("io.wispforest:owo-sentinel:${property("deps.owo_lib")}")
    modules("key-binding-api-v1", "lifecycle-events-v1")

    // Testing
    modLocalRuntime("maven.modrinth:simple-voice-chat:${property("deps.simple_vc")}")
    modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
//    modLocalRuntime("me.djtheredstoner:DevAuth-fabric:${property("test.devauth")}")
    vineflowerDecompilerClasspath("org.vineflower:vineflower:1.10.0")
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/soundboard.accesswidener")

    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
}

java {
    withSourcesJar()
}

tasks.processResources {
    inputs.property("id", mod.id)
    inputs.property("name", mod.name)
    inputs.property("version", mod.version)
    inputs.property("mcdep", mcDep)

    val map = mapOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "mcdep" to mcDep
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

afterEvaluate {
    loom {
        runs {
            configureEach {
                vmArgs("-Xmx2G", "-XX:+UseShenandoahGC")

//                property("mixin.debug", "true")
//                property("mixin.debug.export.decompile", "false")
//                property("mixin.debug.verbose", "true")
                property("mixin.dumpTargetOnFailure", "true")
                // makes silent failures into hard-failures
//                property("mixin.checks", "true")
//                property("mixin.hotSwap", "true")

                val mixinJarFile = configurations.compileClasspath.get().files {
                    it.group == "net.fabricmc" && it.name == "sponge-mixin"
                }.firstOrNull()
                if (mixinJarFile != null)
                    vmArg("-javaagent:$mixinJarFile")
            }
        }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName = "${mod.name} ${mod.version} for $mcVersion"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
//    type = STABLE
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN")
        .getOrNull() == null || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(mcVersion)
        requires {
            slug = "fabric-api"
        }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.add(mcVersion)
        requires {
            slug = "fabric-api"
        }
    }
}
/*
publishing {
    repositories {
        maven("...") {
            name = "..."
            credentials(PasswordCredentials::class.java)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${property("mod.group")}.${mod.id}"
            artifactId = mod.version
            version = mcVersion

            from(components["java"])
        }
    }
}
*/