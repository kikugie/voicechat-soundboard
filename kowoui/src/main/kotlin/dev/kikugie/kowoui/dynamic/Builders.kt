package dev.kikugie.kowoui.dynamic

import io.wispforest.owo.ui.core.Component
import net.minecraft.text.Text

inline infix fun <T : Component> T.wrap(build: WrapperContainer<T>.() -> Unit) =
    WrapperContainer(this).apply(build)

@JvmOverloads inline fun fixedSpacer(pixels: Int, build: FixedSpacerComponent.() -> Unit = {}) =
    FixedSpacerComponent(pixels).apply(build)

@JvmOverloads inline fun dynamicButton(text: Text = Text.empty(), build: DynamicButtonComponent.() -> Unit = {}) =
    DynamicButtonComponent(text).apply(build)

@JvmOverloads inline fun dynamicLabel(text: Text = Text.empty(), build: DynamicLabelComponent.() -> Unit = {}) =
    DynamicLabelComponent(text).apply(build)

@JvmOverloads inline fun coloredTextBox(build: ColoredTextComponent.() -> Unit = {}) =
    ColoredTextComponent().apply(build)