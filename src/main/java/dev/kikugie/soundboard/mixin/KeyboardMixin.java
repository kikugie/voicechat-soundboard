package dev.kikugie.soundboard.mixin;

import dev.kikugie.soundboard.ModKeyBinds;
import net.minecraft.client.Keyboard;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0), cancellable = true)
	private void cancelOnKeyCombo(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		boolean isNumrow = key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9;
		boolean isNumpad = key >= GLFW.GLFW_KEY_KP_0 && key <= GLFW.GLFW_KEY_KP_9;
		if (!isNumrow && !isNumpad) return;

		KeyBinding play = ModKeyBinds.get("play");
		if (play != null && play.isPressed()) ci.cancel();
	}
}
