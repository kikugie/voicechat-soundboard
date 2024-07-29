package dev.kikugie.soundboard.gui.component

import dev.kikugie.soundboard.util.drawLinePrecise
import dev.kikugie.soundboard.util.volumeScale
import io.wispforest.owo.ui.base.BaseComponent
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.OwoUIDrawContext
import io.wispforest.owo.ui.core.Sizing
import kotlin.math.absoluteValue
import kotlin.properties.Delegates.observable

class WaveformComponent(
    private val data: ShortArray,
    scalar: Double = 1.0
) : BaseComponent() {
    var thickness: Double = 1.0
    var color: Color = Color.ofRgb(0x1976D2)
    var mult: Double by observable(scalar) {_, _, _ ->
        update()
    }
    private var lastHeight: Int = 0
    private var lines: IntArray = intArrayOf()

    init {
        sizing(Sizing.fill())
    }

    override fun applySizing() {
        super.applySizing()
        if (lines.size != width || lastHeight != height)
            update()
    }

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        if (lines.isEmpty()) return
        val local = margins.get()
        var drawX = x + local.right + .5
        val drawY = y + local.top + .0
        for (i in 0..<lines.lastIndex) context.drawLinePrecise(
            drawX,
            drawY + lines[i],
            ++drawX,
            drawY + lines[i + 1],
            thickness,
            color
        )
    }

    private fun update() {
        val compressed = compress(data, width)
        val max = compressed.maxBy { it.absoluteValue }.absoluteValue
        lines = compressed.map {
            point((it * mult.volumeScale).coerceIn(-max, max), max)
        }.toIntArray()
        lastHeight = height
    }

    private fun compress(array: ShortArray, length: Int): DoubleArray {
        val window = array.size / length.toDouble()
        return DoubleArray(length) {
            val start = (it * window).toInt()
            val end = ((it + 1) * window).toInt().coerceAtMost(data.size)
            array.copyOfRange(start, end).average()
        }
    }

    private fun point(it: Double, max: Double): Int {
        val halfHeight = height / 2F
        val lineHeight = it / max * halfHeight
        return (halfHeight - lineHeight).toInt()
    }
}