package dev.kikugie.kowoui.dynamic

import io.wispforest.owo.ui.core.Component
import net.minecraft.text.Text

@JvmOverloads inline fun <T : Component> T.wrap(build: WrapperContainer<T>.() -> Unit = {}) =
    WrapperContainer(this).apply(build)

@JvmOverloads inline fun dynamicButton(text: Text = Text.empty(), build: DynamicButtonComponent.() -> Unit = {}) =
    DynamicButtonComponent(text).apply(build)

@JvmOverloads inline fun dynamicLabel(text: Text = Text.empty(), build: DynamicLabelComponent.() -> Unit = {}) =
    DynamicLabelComponent(text).apply(build)