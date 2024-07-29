package dev.kikugie.soundboard.mixin;

import dev.kikugie.soundboard.access.TextFieldAccessor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Renders the text red if the {@link TextFieldAccessor#soundboard$isValid(String)} returns false
 */
@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin implements TextFieldAccessor {
    @Shadow
    private String text;

    @Override
    public boolean soundboard$isValid(String text) {
        return true;
    }

    @ModifyVariable(method = "renderWidget", at = @At("STORE"), ordinal = 2)
    private int makeRedIfInvalid(int value) {
        return soundboard$isValid(text) ? value : 0xFFFF0000;
    }
}