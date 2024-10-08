package dev.kikugie.kowoui.mixin.owo;

import io.wispforest.owo.ui.component.LabelComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @see <a href="https://github.com/wisp-forest/owo-lib/issues/319">OwO-lib issue report</a>
 */
@Mixin(value = LabelComponent.class, remap = false)
public class LabelComponentMixin {
	@Shadow protected List<OrderedText> wrappedText;

	@Inject(method = "styleAt", at = @At(value = "HEAD"), cancellable = true)
	private void preventOutOfBounds(int mouseX, int mouseY, CallbackInfoReturnable<Style> cir) {
		if (this.wrappedText.isEmpty()) cir.setReturnValue(Style.EMPTY);
	}
}
