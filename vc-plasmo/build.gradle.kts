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

dependencies {
    project(path = ":", configuration = "namedElements").also { include(implementation(it)!!) }
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)

    compileOnly(libs.plasmovc.api)
    compileOnly(libs.plasmovc.config)
    modRuntimeOnly(libs.plasmovc)
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