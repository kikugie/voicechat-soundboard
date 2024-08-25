package dev.kikugie.soundboard.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.kikugie.soundboard.audio.download.Downloader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@WrapMethod(method = "method_19862")
	private void downloaderWarning(ButtonWidget button, Operation<Void> operation) {
		var downloads = Downloader.downloads();
		if (client == null || downloads.isEmpty()) operation.call(button);
		else client.setScreen(Downloader.confirmation(this, () -> operation.call(button)));
	}
}
