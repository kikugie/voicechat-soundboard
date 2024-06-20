package dev.kikugie.soundboard.gui

import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.util.modId
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.container.FlowLayout

class InGameConfig : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, BROWSER) {
    val config = Soundboard.config

    override fun build(root: FlowLayout) {
        // TODO
    }

    override fun close() {
        config.save()
        super.close()
    }

    companion object : ScreenManager(InGameConfig::class) {
        val BROWSER = modId("play-config")
    }
}