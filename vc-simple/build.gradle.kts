import me.modmuss50.mpp.ReleaseType

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
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

dependencies {
    include(project(":"))
    include(project(":kowoui"))
    implementation(project(path = ":", configuration = "namedElements"))
    runtimeOnly(project(path = ":kowoui", configuration = "namedElements"))
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)

    compileOnly(libs.simplevc.api)
    modRuntimeOnly(libs.simplevc)
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
        "version" to mod.version
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    displayName = "${mod.name} ${mod.version}"
    version = mod.version
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = ReleaseType.of(project.property("release").toString())
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null ||
            providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(libs.versions.minecraft)
        requires { slug = "fabric-api" }
        requires { slug = "fabric-language-kotlin" }
        requires { slug = "simple-voice-chat" }
        requires { slug = "owo-lib" }
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.add(libs.versions.minecraft)
        requires { slug = "fabric-api" }
        requires { slug = "fabric-language-kotlin" }
        requires { slug = "simple-voice-chat" }
        requires { slug = "owo-lib" }
    }
}