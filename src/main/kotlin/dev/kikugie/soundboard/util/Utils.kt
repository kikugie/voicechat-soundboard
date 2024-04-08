package dev.kikugie.soundboard.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding

inline fun keybind(key: Int, translation: String, category: String, crossinline action: () -> Unit) =
    KeyBindingHelper.registerKeyBinding(KeyBinding(translation, key, category)).also { binding ->
        ClientTickEvents.END_CLIENT_TICK.register {
            if (binding.wasPressed()) action()
        }
    }
