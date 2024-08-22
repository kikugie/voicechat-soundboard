package dev.kikugie.kowoui.mixin;

import dev.kikugie.kowoui.mixinstuff.TextFieldAccessor;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin implements TextFieldAccessor {
	@Unique
	private String cachedText;

	@Unique
	private int cachedColor;

	@Shadow
	private String text;

	@Shadow
	private Predicate<String> textPredicate;

	@Override
	public Predicate<String> soundboard$predicate() {
		return textPredicate;
	}

	@ModifyVariable(method = "renderWidget", at = @At("STORE"), ordinal = 2)
	private int applyColor(int value) {
		if (text.equals(cachedText)) return cachedColor;
		Color color = soundboard$color(text);
		cachedColor = color == null ? value : color.argb();
		cachedText = text;
		return cachedColor;
	}
}