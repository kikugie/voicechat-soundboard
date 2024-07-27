@file:Suppress("unused")

package dev.kikugie.kowoui

import io.wispforest.owo.ui.component.*
import io.wispforest.owo.ui.component.ButtonComponent.Renderer
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.OverlayContainer
import io.wispforest.owo.ui.container.ScrollContainer
import io.wispforest.owo.ui.core.*
import io.wispforest.owo.ui.util.FocusHandler
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.util.function.Consumer
import java.util.function.Function

private inline fun unsupported(reason: () -> String = { "" }): Nothing =
    throw UnsupportedOperationException(reason())

val Component.hasParent: Boolean get() = hasParent()
val Component.parent: ParentComponent? get() = parent()
val Component.root: ParentComponent? get() = root()
val Component.focusHandler: FocusHandler? get() = focusHandler()

var Component.id: String?
    get() = id()
    set(value) {
        id(value)
    }

var Component.zIndex: Int
    get() = zIndex()
    set(value) {
        zIndex(value)
    }

val Component.animatablePositioning: AnimatableProperty<Positioning>
    get() = positioning()
var Component.positioning: Positioning
    get() = positioning().get()
    set(value) {
        positioning(value)
    }

val Component.animatableMargins: AnimatableProperty<Insets>
    get() = margins()
var Component.margins: Insets
    get() = margins().get()
    set(value) {
        margins(value)
    }

val Component.animatableHorizontalSizing: AnimatableProperty<Sizing>
    get() = horizontalSizing()
var Component.horizontalSizing: Sizing
    get() = horizontalSizing().get()
    set(value) {
        horizontalSizing(value)
    }

val Component.animatableVerticalSizing: AnimatableProperty<Sizing>
    get() = verticalSizing()
var Component.verticalSizing: Sizing
    get() = verticalSizing().get()
    set(value) {
        verticalSizing(value)
    }

var Component.sizing: Sizing
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        sizing(value)
    }

var Component.tooltip: List<TooltipComponent>?
    get() = tooltip()
    set(value) {
        tooltip(value)
    }
var Component.tooltipText: Text
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        tooltip(value)
    }
var Component.tooltipTexts: Collection<Text>
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        tooltip(value)
    }

var Component.cursorStyle: CursorStyle
    get() = cursorStyle()
    set(value) {cursorStyle(value)}

var ParentComponent.horizontalAlignment: HorizontalAlignment
    get() = horizontalAlignment()
    set(value) {
        horizontalAlignment(value)
    }
var ParentComponent.verticalAlignment: VerticalAlignment
    get() = verticalAlignment()
    set(value) {
        verticalAlignment(value)
    }

val ParentComponent.animatablePadding: AnimatableProperty<Insets> get() = padding()
var ParentComponent.padding: Insets
    get() = padding().get()
    set(value) {
        padding(value)
    }

var ParentComponent.allowOverflow: Boolean
    get() = allowOverflow()
    set(value) {
        allowOverflow(value)
    }

var ParentComponent.surface: Surface
    get() = surface()
    set(value) {
        surface(value)
    }

var FlowLayout.gap: Int
    get() = gap()
    set(value) {
        gap(value)
    }

val ParentComponent.children: List<Component>
    get() = children()

var OverlayContainer<*>.closeOnClick: Boolean
    get() = closeOnClick()
    set(value) {
        closeOnClick(value)
    }

var ScrollContainer<*>.scrollbar: ScrollContainer.Scrollbar
    get() = scrollbar()
    set(value) {
        scrollbar(value)
    }

var ScrollContainer<*>.scrollStep: Int
    get() = scrollStep()
    set(value) {
        scrollStep(value)
    }

var ScrollContainer<*>.fixedScrollbarLength: Int
    get() = fixedScrollbarLength()
    set(value) {
        fixedScrollbarLength(value)
    }

var BoxComponent.fill: Boolean
    get() = fill()
    set(value) {
        fill(value)
    }

var BoxComponent.direction: BoxComponent.GradientDirection
    get() = direction()
    set(value) {
        direction(value)
    }

var BoxComponent.color: Color
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        color(value)
    }

val BoxComponent.animatableStartColor: AnimatableProperty<Color>
    get() = startColor()

val BoxComponent.animatableEndColor: AnimatableProperty<Color>
    get() = endColor()

var BoxComponent.startColor: Color
    get() = startColor().get()
    set(value) {
        startColor(value)
    }

var BoxComponent.endColor: Color
    get() = endColor().get()
    set(value) {
        endColor(value)
    }

var ButtonComponent.renderer: Renderer
    get() = renderer()
    set(value) {
        renderer(value)
    }

var ButtonComponent.textShadow: Boolean
    get() = textShadow()
    set(value) {
        textShadow(value)
    }

var CheckboxComponent.checked: Boolean
    get() = isChecked
    set(value) {
        checked(value)
    }

var ColorPickerComponent.selectedColor: Color
    get() = selectedColor()
    set(value) {
        selectedColor(value)
    }

var ColorPickerComponent.selectorWidth: Int
    get() = selectorWidth()
    set(value) {
        selectorWidth(value)
    }

var ColorPickerComponent.selectorPadding: Int
    get() = selectorPadding()
    set(value) {
        selectorPadding(value)
    }

var ColorPickerComponent.showAlpha: Boolean
    get() = showAlpha()
    set(value) {
        showAlpha(value)
    }

var DiscreteSliderComponent.discreteValue: Double
    get() = discreteValue()
    set(value) {
        setFromDiscreteValue(value)
    }

var DiscreteSliderComponent.decimalPlaces: Int
    get() = decimalPlaces()
    set(value) {
        decimalPlaces(value)
    }

val DiscreteSliderComponent.min: Double
    get() = min()

val DiscreteSliderComponent.max: Double
    get() = max()

var DropdownComponent.closeWhenNotHovered: Boolean
    get() = closeWhenNotHovered()
    set(value) {
        closeWhenNotHovered(value)
    }

var EntityComponent<*>.allowMouseRotation: Boolean
    get() = allowMouseRotation()
    set(value) {
        allowMouseRotation(value)
    }

var EntityComponent<*>.lookAtCursor: Boolean
    get() = lookAtCursor()
    set(value) {
        lookAtCursor(value)
    }

var EntityComponent<*>.scaleToFit: Boolean
    get() = scaleToFit()
    set(value) {
        scaleToFit(value)
    }

var EntityComponent<*>.showNametag: Boolean
    get() = showNametag()
    set(value) {
        showNametag(value)
    }

var EntityComponent<*>.scale: Float
    get() = scale()
    set(value) {
        scale(value)
    }

val EntityComponent<*>.transform: Consumer<MatrixStack>
    get() = transform()

var ItemComponent.tooltipFromStack: Boolean
    get() = setTooltipFromStack()
    set(value) {
        setTooltipFromStack(value)
    }

var ItemComponent.stack: ItemStack
    get() = stack()
    set(value) {
        stack(value)
    }

var ItemComponent.showOverlay: Boolean
    get() = showOverlay()
    set(value) {
        showOverlay(value)
    }

var LabelComponent.text: Text
    get() = text()
    set(value) {
        text(value)
    }

var LabelComponent.maxWidth: Int
    get() = maxWidth()
    set(value) {
        maxWidth(value)
    }

var LabelComponent.shadow: Boolean
    get() = shadow()
    set(value) {
        shadow(value)
    }

var LabelComponent.horizontalTextAlignment: HorizontalAlignment
    get() = horizontalTextAlignment()
    set(value) {
        horizontalTextAlignment(value)
    }

var LabelComponent.verticalTextAlignment: VerticalAlignment
    get() = verticalTextAlignment()
    set(value) {
        verticalTextAlignment(value)
    }

val LabelComponent.animatableColor: AnimatableProperty<Color>
    get() = color()

var LabelComponent.color: Color
    get() = color().get()
    set(value) {
        color(value)
    }

var LabelComponent.lineHeight: Int
    get() = lineHeight()
    set(value) {
        lineHeight(value)
    }

var LabelComponent.lineSpacing: Int
    get() = lineSpacing()
    set(value) {
        lineSpacing(value)
    }

val LabelComponent.textClickHandler: Function<Style, Boolean>
    get() = textClickHandler()

var SliderComponent.value: Double
    get() = value()
    set(value) {
        value(value)
    }

var SliderComponent.scrollStep: Double
    get() = scrollStep()
    set(value) {
        scrollStep(value)
    }

var SliderComponent.messageProvider: Function<String, Text>
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        message(value)
    }

var SlimSliderComponent.value: Double
    get() = value()
    set(value) {
        value(value)
    }

var SlimSliderComponent.stepSize: Double
    get() = stepSize()
    set(value) {
        stepSize(value)
    }

var SlimSliderComponent.tooltipSupplier: Function<Double, Text>
    get() = tooltipSupplier()
    set(value) {
        tooltipSupplier(value)
    }

val SlimSliderComponent.min: Double
    get() = min()

val SlimSliderComponent.max: Double
    get() = max()

var SmallCheckboxComponent.checked: Boolean
    get() = checked()
    set(value) {
        checked(value)
    }

var SmallCheckboxComponent.label: Text
    get() = label()
    set(value) {
        label(value)
    }

var SmallCheckboxComponent.labelShadow: Boolean
    get() = labelShadow()
    set(value) {
        labelShadow(value)
    }

var SpriteComponent.blend: Boolean
    get() = blend()
    set(value) {
        blend(value)
    }

var TextAreaComponent.maxLines: Int
    get() = maxLines()
    set(value) {
        maxLines(value)
    }

val TextAreaComponent.heightOffset: Int
    get() = heightOffset()

var TextBoxComponent.drawBackground: Boolean
    get() = drawsBackground()
    set(value) {
        setDrawsBackground(value)
    }

val TextureComponent.animatableVisibleArea: AnimatableProperty<PositionedRectangle>
    get() = visibleArea()

var TextureComponent.visibleArea: PositionedRectangle
    get() = visibleArea().get()
    set(value) {
        visibleArea(value)
    }

var TextureComponent.blend: Boolean
    get() = blend()
    set(value) {
        blend(value)
    }