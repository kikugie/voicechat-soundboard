package dev.kikugie.kowoui.dynamic

import io.wispforest.owo.ui.container.WrappingParentComponent
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Sizing.content

class WrapperContainer<T : Component>(child: T) : WrappingParentComponent<T>(content(), content(), child)