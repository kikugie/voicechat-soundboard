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
    include(project(":"))
    implementation(project(path = ":", configuration = "namedElements"))
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
    inputs.property("core", property("version").toString())

    val map = mapOf(
        "version" to mod.version,
        "core" to property("version").toString()
    )

    filesMatching("fabric.mod.json") { expand(map) }
}