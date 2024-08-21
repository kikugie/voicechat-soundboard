package dev.kikugie.soundboard.mixin.owo_ui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.soundboard.util.UtilKt;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(value = OwoUIDrawContext.class, remap = false)
public class OwODrawContextMixin {
	@WrapOperation(method = "drawInspector", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getSimpleName()Ljava/lang/String;"))
	private String getAccurateName(Class<? extends Component> cls, Operation<String> original) {
		return UtilKt.accurateName(cls);
	}
}
