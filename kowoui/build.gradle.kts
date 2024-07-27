plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
}

class ModData {
    val id: String = project.property("kowoui.id").toString()
    val name: String = project.property("kowoui.name").toString()
    val group: String = project.property("group").toString()
    val version: String = project.property("kowoui.version").toString()
}
val mod = ModData()

version = "${mod.version}+${libs.versions.minecraft.get()}"
group = mod.group
base { archivesName.set(mod.id) }

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)
    modApi(libs.owo) {
        exclude(group = "net.fabricmc")
    }
}

tasks.processResources {
    inputs.property("id", mod.id)
    inputs.property("name", mod.name)
    inputs.property("version", mod.version)
    inputs.property("minecraft", project.property("mcdep").toString())
    inputs.property("flk", project.property("flk").toString())
    inputs.property("owolib", project.property("owolib").toString())

    val map = mapOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to project.property("mcdep").toString(),
        "flk" to project.property("flk").toString(),
        "owolib" to project.property("owolib").toString(),
    )

    filesMatching("fabric.mod.json") { expand(map) }
}