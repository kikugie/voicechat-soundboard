package dev.kikugie.soundboard.mixin.owo_ui;

import io.wispforest.owo.ui.container.ScrollContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ScrollContainer.class, remap = false)
public interface ScrollContainerAccessor {
    @Accessor
    double getScrollOffset();

    @Invoker
    void invokeScrollBy(double offset, boolean instant, boolean showScrollbar);
}
