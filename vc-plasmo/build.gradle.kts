import me.modmuss50.mpp.ReleaseType

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.mpp)
}

class ModData {
    val id: String by project
    val name: String by project
    val group: String by project
    val version: String by project
}
val mod = ModData()

version = "${mod.version}+${libs.versions.minecraft.get()}"
group = mod.group
base { archivesName.set(mod.id) }

dependencies {
    include(project(":"))
    implementation(project(path = ":", configuration = "namedElements"))
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)

    compileOnly(libs.plasmovc.api)
    compileOnly(libs.plasmovc.config)
//    modRuntimeOnly(libs.plasmovc)
}

loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../run"
    }
}

tasks.processResources {
    inputs.property("version", mod.version)

    val map = mapOf(
        "version" to mod.version,
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

tasks.register<Copy>("applyPlasmoMod") {
    from(rootProject.file("mods/plasmovoice-2.1.0-SNAPSHOT.jar"))
    into(rootProject.file("run/mods"))
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName = "${mod.name} ${mod.version}"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = ReleaseType.of(project.property("release").toString())
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN")
        .getOrNull() == null || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null
    dryRun = true

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(libs.versions.minecraft)
        requires { slug = "fabric-api" }
        requires { slug = "fabric-language-kotlin" }
        requires { slug = "plasmo-voice" }
        requires { slug = "owo-lib" }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.add(libs.versions.minecraft)
        requires { slug = "fabric-api" }
        requires { slug = "fabric-language-kotlin" }
        requires { slug = "plasmo-voice" }
        requires { slug = "owo-lib" }
    }
}
