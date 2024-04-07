package dev.kikugie.soundboard

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.SemanticVersion
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint

object SoundboardPrelaunch : PreLaunchEntrypoint {
    const val API_REQUIREMENT = "2.5"
    override fun onPreLaunch() {
        val vc = FabricLoader.getInstance().getModContainer("voicechat").get()
        val api = vc.metadata.version.friendlyString.substringAfter('-')
        val parsedRequired = SemanticVersion.parse(API_REQUIREMENT)
        val parsedPresent = SemanticVersion.parse(api)
        if (parsedRequired > parsedPresent as Version) throw AssertionError(
            "Expected voicechat API version of >=$API_REQUIREMENT, received ${api}. Update `simple-voice-chat` to its latest version!"
        )
    }
}