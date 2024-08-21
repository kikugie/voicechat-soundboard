package dev.kikugie.soundboard

import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val MOD_ID = "soundboard"
const val VERSION = "0.3.0"
val LOGGER: Logger = LoggerFactory.getLogger("Soundboard")
val CONFIG get() = Soundboard.config