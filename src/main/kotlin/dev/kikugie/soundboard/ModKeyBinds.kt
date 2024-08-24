package dev.kikugie.soundboard

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.option.KeyBinding

object ModKeyBinds {
    private val keybinds = mutableMapOf<String, KeyBuilder>()

    @JvmStatic
    operator fun get(name: String): KeyBinding? = keybinds[name]?.keybind
    fun keybind(key: Int, name: String, action: KeyBuilder.() -> Unit) {
        val bind = KeyBindingHelper.registerKeyBinding(KeyBinding("soundboard.keybinds.$name", key, "soundboard.title"))
        val builder = KeyBuilder(bind).apply(action)
        keybinds[name] = builder
        ClientTickEvents.END_CLIENT_TICK.register {
            if (bind.wasPressed()) builder.inGame?.invoke()
        }
    }

    internal fun invoke(screen: Screen) = keybinds.values.forEach {
        it.inGui?.invoke(screen)
    }

    class KeyBuilder(
        val keybind: KeyBinding,
    ) {
        internal var inGame: (() -> Unit)? = null
        internal var inGui: ((Screen) -> Unit)? = null

        fun inGame(action: (KeyBinding) -> Unit) {
            inGame = { action(keybind) }
        }

        fun inGui(action: Screen.(KeyBinding) -> Unit) {
            inGui = { it.action(keybind) }
        }
    }
}