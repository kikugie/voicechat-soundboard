package dev.kikugie.kowoui.mixin.owo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.util.FocusHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Idr what causes this
 */
@Mixin(value = BaseParentComponent.class, remap = false)
public class BaseParentComponentMixin {
	@WrapOperation(method = "drawChildren", at = @At(value = "INVOKE", target = "Lio/wispforest/owo/ui/util/FocusHandler;lastFocusSource()Lio/wispforest/owo/ui/core/Component$FocusSource;"))
	private Component.FocusSource fixNullReference(FocusHandler instance, Operation<Component.FocusSource> original) {
		return instance == null ? null : original.call(instance);
	}
}
