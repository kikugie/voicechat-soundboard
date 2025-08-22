plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.yamlang)
    alias(libs.plugins.mpp)
}

class ModData {
    val id: String = project.property("id").toString()
    val name: String = project.property("name").toString()
    val group: String = project.property("group").toString()
    val version: String = project.property("version").toString()
}

val mod = ModData()

version = "${mod.version}+${libs.versions.minecraft.get()}"
group = mod.group
base { archivesName.set(mod.id) }

allprojects {
    repositories {
        fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
            forRepository { maven(url) }
            filter { groups.forEach(::includeGroup) }
        }
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
        strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
        strictMaven("https://maven.wispforest.io", "io.wispforest", "io.wispforest.endec")
        strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc")
        strictMaven("https://maven.maxhenkel.de/releases", "de.maxhenkel.voicechat")
        strictMaven("https://repo.plasmoverse.com/snapshots", "su.plo.voice", "su.plo.voice.api", "su.plo.slib")
        strictMaven("https://repo.plasmoverse.com/releases", "su.plo.config")
    }
}

dependencies {
    implementation(project(path = "kowoui", configuration = "namedElements"))
    fun modules(vararg modules: String) {
        modules.forEach { modApi(fabricApi.module("fabric-$it", libs.versions.fabric.api.get())) }
    }
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)

    libs.okhttp.let {
        include(it)
        api(it)
    }

    include(libs.okio)
    include(libs.owo.sentinel)
    implementation("com.googlecode.soundlibs:mp3spi:${project.property("mp3spi_version")}") {
        exclude(group = "junit", module = "junit")
    }
    include("com.googlecode.soundlibs:mp3spi:${project.property("mp3spi_version")}")

    // Tritonus dependencies required by mp3spi
    implementation("com.googlecode.soundlibs:tritonus-share:${project.property("tritonus_version")}") {
        exclude(group = "junit", module = "junit")
    }
    include("com.googlecode.soundlibs:tritonus-share:${project.property("tritonus_version")}")

    implementation("com.googlecode.soundlibs:jlayer:${project.property("jlayer_version")}") {
        exclude(group = "junit", module = "junit")
    }
    include("com.googlecode.soundlibs:jlayer:${project.property("jlayer_version")}")

    implementation("org.microhttp:microhttp:${project.property("microhttp_version")}")
    include("org.microhttp:microhttp:${project.property("microhttp_version")}")
    modApi(libs.fabric.api)
    modApi(libs.modmenu)
    modApi(libs.owo) {
        exclude(group = "net.fabricmc")
    }
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
    inputs.property("minecraft", project.property("mcdep").toString())
    inputs.property("flk", project.property("flk").toString())
    inputs.property("owolib", project.property("owolib").toString())

    val map = mapOf(
        "version" to mod.version,
        "minecraft" to project.property("mcdep").toString(),
        "flk" to project.property("flk").toString(),
        "owolib" to project.property("owolib").toString(),
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
    owolibRichTranslations = true
}

java {
    withSourcesJar()
}

//publishMods {
//    val files = mutableListOf<Provider<RegularFile>>()
//    subprojects.mapTo(files) { it.tasks.remapJar.get().archiveFile }
//    subprojects.mapTo(files) { it.tasks.remapSourcesJar.get().archiveFile }
//    file = files.first()
//    additionalFiles.from(*files.drop(1).toTypedArray())
//    displayName = "${mod.name} ${mod.version}"
//    version = mod.version
//    changelog = rootProject.file("CHANGELOG.md").readText()
//    type = ReleaseType.of(project.property("release").toString())
//    modLoaders.add("fabric")
//
//    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null ||
//            providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null ||
//            providers.environmentVariable("GITHUB_TOKEN").getOrNull() == null
//    dryRun = true
//
//    github {
//        repository = "kikugie/voicechat-soundboard"
//        accessToken = providers.environmentVariable("GITHUB_TOKEN")
//        commitish = "multiaddon"
//    }
//}
