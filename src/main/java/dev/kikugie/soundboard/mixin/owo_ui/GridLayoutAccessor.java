package dev.kikugie.soundboard.mixin.owo_ui;

import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GridLayout.class, remap = false)
public interface GridLayoutAccessor {
    @Accessor
    int getColumns();

    @Accessor
    int getRows();

    @Accessor
    @Mutable
    void setRows(int value);

    @Accessor
    Component[] getChildren();

    @Accessor
    @Mutable
    void setChildren(Component[] children);
}
