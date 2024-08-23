@file:Suppress("unused")

package dev.kikugie.kowoui.access

import dev.kikugie.kowoui.unsupported
import io.wispforest.owo.ui.component.*
import io.wispforest.owo.ui.component.ButtonComponent.Renderer
import io.wispforest.owo.ui.core.*
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.util.function.Consumer
import java.util.function.Function


var EntityComponent<*>.allowMouseRotation: Boolean
    get() = allowMouseRotation()
    set(value) {
        allowMouseRotation(value)
    }

var SpriteComponent.blend: Boolean
    get() = blend()
    set(value) {
        blend(value)
    }

var TextureComponent.blend: Boolean
    get() = blend()
    set(value) {
        blend(value)
    }

var CheckboxComponent.checked: Boolean
    get() = isChecked
    set(value) {
        checked(value)
    }

var SmallCheckboxComponent.checked: Boolean
    get() = checked()
    set(value) {
        checked(value)
    }

var DropdownComponent.closeWhenNotHovered: Boolean
    get() = closeWhenNotHovered()
    set(value) {
        closeWhenNotHovered(value)
    }

var LabelComponent.color: Color
    get() = color().get()
    set(value) {
        color(value)
    }

var BoxComponent.color: Color
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        color(value)
    }

var DiscreteSliderComponent.decimalPlaces: Int
    get() = decimalPlaces()
    set(value) {
        decimalPlaces(value)
    }

var BoxComponent.direction: BoxComponent.GradientDirection
    get() = direction()
    set(value) {
        direction(value)
    }

var DiscreteSliderComponent.discreteValue: Double
    get() = discreteValue()
    set(value) {
        setFromDiscreteValue(value)
    }

var TextBoxComponent.drawBackground: Boolean
    get() = drawsBackground()
    set(value) {
        setDrawsBackground(value)
    }

var BoxComponent.endColor: Color
    get() = endColor().get()
    set(value) {
        endColor(value)
    }

var BoxComponent.fill: Boolean
    get() = fill()
    set(value) {
        fill(value)
    }

var LabelComponent.horizontalTextAlignment: HorizontalAlignment
    get() = horizontalTextAlignment()
    set(value) {
        horizontalTextAlignment(value)
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

var EntityComponent<*>.lookAtCursor: Boolean
    get() = lookAtCursor()
    set(value) {
        lookAtCursor(value)
    }

var TextAreaComponent.maxLines: Int
    get() = maxLines()
    set(value) {
        maxLines(value)
    }

var LabelComponent.maxWidth: Int
    get() = maxWidth()
    set(value) {
        maxWidth(value)
    }

var SliderComponent.messageProvider: Function<String, Text>
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        message(value)
    }

var ButtonComponent.renderer: Renderer
    get() = renderer()
    set(value) {
        renderer(value)
    }

var EntityComponent<*>.scale: Float
    get() = scale()
    set(value) {
        scale(value)
    }

var EntityComponent<*>.scaleToFit: Boolean
    get() = scaleToFit()
    set(value) {
        scaleToFit(value)
    }

var SliderComponent.scrollStep: Double
    get() = scrollStep()
    set(value) {
        scrollStep(value)
    }

var ColorPickerComponent.selectedColor: Color
    get() = selectedColor()
    set(value) {
        selectedColor(value)
    }

var ColorPickerComponent.selectorPadding: Int
    get() = selectorPadding()
    set(value) {
        selectorPadding(value)
    }

var ColorPickerComponent.selectorWidth: Int
    get() = selectorWidth()
    set(value) {
        selectorWidth(value)
    }

var LabelComponent.shadow: Boolean
    get() = shadow()
    set(value) {
        shadow(value)
    }

var ColorPickerComponent.showAlpha: Boolean
    get() = showAlpha()
    set(value) {
        showAlpha(value)
    }

var EntityComponent<*>.showNametag: Boolean
    get() = showNametag()
    set(value) {
        showNametag(value)
    }

var ItemComponent.showOverlay: Boolean
    get() = showOverlay()
    set(value) {
        showOverlay(value)
    }

var ItemComponent.stack: ItemStack
    get() = stack()
    set(value) {
        stack(value)
    }

var BoxComponent.startColor: Color
    get() = startColor().get()
    set(value) {
        startColor(value)
    }

var SlimSliderComponent.stepSize: Double
    get() = stepSize()
    set(value) {
        stepSize(value)
    }

var LabelComponent.text: Text
    get() = text()
    set(value) {
        text(value)
    }

var ButtonComponent.textShadow: Boolean
    get() = textShadow()
    set(value) {
        textShadow(value)
    }

var ItemComponent.tooltipFromStack: Boolean
    get() = setTooltipFromStack()
    set(value) {
        setTooltipFromStack(value)
    }

var SlimSliderComponent.tooltipSupplier: Function<Double, Text>
    get() = tooltipSupplier()
    set(value) {
        tooltipSupplier(value)
    }

var SlimSliderComponent.value: Double
    get() = value()
    set(value) {
        value(value)
    }

var SliderComponent.value: Double
    get() = value()
    set(value) {
        value(value)
    }

var LabelComponent.verticalTextAlignment: VerticalAlignment
    get() = verticalTextAlignment()
    set(value) {
        verticalTextAlignment(value)
    }

var TextureComponent.visibleArea: PositionedRectangle
    get() = visibleArea().get()
    set(value) {
        visibleArea(value)
    }

var TextFieldWidget.placeholder: Text?
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        setPlaceholder(value)
    }

var TextFieldWidget.suggestion: String?
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        setSuggestion(value)
    }

var TextFieldWidget.maxLength: Int
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        setMaxLength(value)
    }

val LabelComponent.animatableColor: AnimatableProperty<Color>
    get() = color()
val BoxComponent.animatableEndColor: AnimatableProperty<Color>
    get() = endColor()
val BoxComponent.animatableStartColor: AnimatableProperty<Color>
    get() = startColor()
val TextureComponent.animatableVisibleArea: AnimatableProperty<PositionedRectangle>
    get() = visibleArea()
val TextAreaComponent.heightOffset: Int
    get() = heightOffset()
val SlimSliderComponent.max: Double
    get() = max()
val DiscreteSliderComponent.max: Double
    get() = max()
val SlimSliderComponent.min: Double
    get() = min()
val DiscreteSliderComponent.min: Double
    get() = min()
val LabelComponent.textClickHandler: Function<Style, Boolean>
    get() = textClickHandler()
val EntityComponent<*>.transform: Consumer<MatrixStack>
    get() = transform()
val <T : Entity> EntityComponent<T>.entity
    get() = entity()

fun EntityComponent<*>.transformWith(action: MatrixStack.() -> Unit) {
    transform(action)
}