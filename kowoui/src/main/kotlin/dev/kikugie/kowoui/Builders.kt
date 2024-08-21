@file:Suppress("UnstableApiUsage")

package dev.kikugie.kowoui

import io.wispforest.owo.ui.component.*
import io.wispforest.owo.ui.component.SlimSliderComponent.Axis
import io.wispforest.owo.ui.container.*
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.Identifier

@JvmOverloads inline fun stack(build: StackLayout.() -> Unit = {}): StackLayout =
    Containers.stack(Sizing.content(), Sizing.content()).apply(build)

@JvmOverloads inline fun grid(rows: Int, columns: Int, build: GridLayout.() -> Unit = {}): GridLayout =
    Containers.grid(Sizing.content(), Sizing.content(), rows, columns).apply(build)

@JvmOverloads inline fun horizontalFlow(build: FlowLayout.() -> Unit = {}): FlowLayout =
    Containers.horizontalFlow(Sizing.content(), Sizing.content()).apply(build)

@JvmOverloads inline fun verticalFlow(build: FlowLayout.() -> Unit = {}): FlowLayout =
    Containers.verticalFlow(Sizing.content(), Sizing.content()).apply(build)

@JvmOverloads inline fun ltrTextFlow(build: FlowLayout.() -> Unit = {}): FlowLayout =
    Containers.ltrTextFlow(Sizing.content(), Sizing.content()).apply(build)

@JvmOverloads inline fun <T : Component> horizontalScroll(child: T, build: ScrollContainer<T>.() -> Unit = {}): ScrollContainer<T> =
    Containers.horizontalScroll(Sizing.content(), Sizing.content(), child).apply(build)

@JvmOverloads inline fun <T : Component> verticalScroll(child: T, build: ScrollContainer<T>.() -> Unit = {}): ScrollContainer<T> =
    Containers.verticalScroll(Sizing.content(), Sizing.content(), child).apply(build)

@JvmOverloads inline fun <T : Component> draggable(child: T, build: DraggableContainer<T>.() -> Unit = {}): DraggableContainer<T> =
    Containers.draggable(Sizing.content(), Sizing.content(), child).apply(build)

@JvmOverloads inline fun collapsible(title: Text, expanded: Boolean, build: CollapsibleContainer.() -> Unit = {}): CollapsibleContainer =
    Containers.collapsible(Sizing.content(), Sizing.content(), title, expanded).apply(build)

@JvmOverloads inline fun <T : Component> overlay(child: T, build: OverlayContainer<T>.() -> Unit = {}): OverlayContainer<T> =
    Containers.overlay(child).apply(build)

@JvmOverloads inline fun <T : Component> renderEffect(child: T, build: RenderEffectWrapper<T>.() -> Unit = {}): RenderEffectWrapper<T> =
    Containers.renderEffect(child).apply(build)

@JvmOverloads inline fun button(build: ButtonComponent.() -> Unit = {}): ButtonComponent =
    Components.button("".text()) {}.apply(build)

@JvmOverloads inline fun button(text: Text, build: ButtonComponent.() -> Unit = {}): ButtonComponent =
    Components.button(text) {}.apply(build)

@JvmOverloads inline fun textBox(build: TextBoxComponent.() -> Unit = {}): TextBoxComponent =
    Components.textBox(Sizing.content()).apply(build)

@JvmOverloads inline fun textArea(build: TextAreaComponent.() -> Unit = {}): TextAreaComponent =
    Components.textArea(Sizing.content(), Sizing.content()).apply(build)

@JvmOverloads inline fun <T : Entity> entity(entity: T, build: EntityComponent<T>.() -> Unit = {}): EntityComponent<T> =
    Components.entity(Sizing.content(), entity).apply(build)

@JvmOverloads inline fun <T : Entity> entity(type: EntityType<T>, nbt: NbtCompound?, build: EntityComponent<T>.() -> Unit = {}): EntityComponent<T> =
    Components.entity(Sizing.content(), type, nbt).apply(build)

@JvmOverloads inline fun item(stack: ItemStack, build: ItemComponent.() -> Unit = {}): ItemComponent =
    Components.item(stack).apply(build)

@JvmOverloads inline fun block(state: BlockState, build: BlockComponent.() -> Unit = {}): BlockComponent =
    Components.block(state).apply(build)

@JvmOverloads inline fun block(state: BlockState, blockEntity: BlockEntity, build: BlockComponent.() -> Unit = {}): BlockComponent =
    Components.block(state, blockEntity).apply(build)

@JvmOverloads inline fun block(state: BlockState, nbt: NbtCompound?, build: BlockComponent.() -> Unit = {}): BlockComponent =
    Components.block(state, nbt).apply(build)

@JvmOverloads inline fun label(build: LabelComponent.() -> Unit = {}): LabelComponent =
    Components.label("".text()).apply(build)

@JvmOverloads inline fun label(text: Text, build: LabelComponent.() -> Unit = {}): LabelComponent =
    Components.label(text).apply(build)

@JvmOverloads inline fun checkbox(text: Text, build: CheckboxComponent.() -> Unit = {}): CheckboxComponent =
    Components.checkbox(text).apply(build)

@JvmOverloads inline fun smallCheckbox(text: Text, build: SmallCheckboxComponent.() -> Unit = {}): SmallCheckboxComponent =
    Components.smallCheckbox(text).apply(build)

@JvmOverloads inline fun slider(build: SliderComponent.() -> Unit = {}): SliderComponent =
    Components.slider(Sizing.content()).apply(build)

@JvmOverloads inline fun slimSlider(axis: Axis, build: SlimSliderComponent.() -> Unit = {}): SlimSliderComponent =
    Components.slimSlider(axis).apply(build)

@JvmOverloads inline fun discreteSlider(min: Double, max: Double, build: DiscreteSliderComponent.() -> Unit = {}): DiscreteSliderComponent =
    Components.discreteSlider(Sizing.content(), min, max).apply(build)

@JvmOverloads inline fun discreteSlider(range: ClosedFloatingPointRange<Double>, build: DiscreteSliderComponent.() -> Unit = {}): DiscreteSliderComponent =
    Components.discreteSlider(Sizing.content(), range.start, range.endInclusive).apply(build)

@JvmOverloads inline fun sprite(id: SpriteIdentifier, build: SpriteComponent.() -> Unit = {}): SpriteComponent =
    Components.sprite(id).apply(build)

@JvmOverloads inline fun sprite(sprite: Sprite, build: SpriteComponent.() -> Unit = {}): SpriteComponent =
    Components.sprite(sprite).apply(build)

@JvmOverloads inline fun texture(
    texture: Identifier,
    u: Int,
    v: Int,
    regionWidth: Int,
    regionHeight: Int,
    textureWidth: Int = 256,
    textureHeight: Int = 256,
    build: TextureComponent.() -> Unit = {},
): TextureComponent =
    Components.texture(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight).apply(build)

@JvmOverloads inline fun box(build: BoxComponent.() -> Unit = {}): BoxComponent =
    Components.box(Sizing.content(), Sizing.content()).apply(build)

@JvmOverloads inline fun dropdown(build: DropdownComponent.() -> Unit = {}): DropdownComponent =
    Components.dropdown(Sizing.content()).apply(build)

@JvmOverloads inline fun spacer(percent: Int = 100, build: SpacerComponent.() -> Unit = {}): SpacerComponent =
    Components.spacer(percent).apply(build)