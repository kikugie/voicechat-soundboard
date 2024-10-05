package dev.kikugie.soundboard.gui

import dev.kikugie.soundboard.CONFIG
import io.wispforest.owo.ui.core.Surface

val CONFIG_PANEL: Surface get() = if (CONFIG.dark) Surface.DARK_PANEL else Surface.PANEL