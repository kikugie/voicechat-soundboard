package dev.kikugie.soundboard.gui.component

import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Size
import io.wispforest.owo.ui.core.Sizing
import it.unimi.dsi.fastutil.ints.IntArraySet
import kotlin.math.max

class FlexibleGridLayout(rows: Int, columns: Int) : GridLayout(Sizing.fill(), Sizing.fill(), rows, columns) {
    private lateinit var columnSizes: IntArray
    private lateinit var rowSizes: IntArray

    override fun childMountingOffset(): Size {
        combine(rows, columns) { row, column ->
            getChild(row, column)?.inflate(Size.of(columnSizes[column], rowSizes[row]))
        }
        return super.childMountingOffset()
    }

    override fun determineSizes(sizes: IntArray, rowSizes: Boolean) =
        if (rowSizes) determineRowSizes(sizes)
        else determineColumnSizes(sizes)

    private fun determineRowSizes(sizes: IntArray) {
        val expands: MutableSet<Int> = IntArraySet(rows)
        val available = height - padding.get().vertical()
        combine(rows, columns) { row, column ->
            val child: Component = getChild(row, column) ?: return@combine
            if (child.verticalSizing().get().isExpand) expands += row
            else sizes[row] = max(sizes[row], child.fullSize().height)
        }

        extracted(expands, sizes, available)
        rowSizes = sizes
    }

    private fun determineColumnSizes(sizes: IntArray) {
        val expands: MutableSet<Int> = IntArraySet(columns)
        val available = width - padding.get().horizontal()
        combine(rows, columns) { row, column ->
            val child: Component = getChild(row, column) ?: return@combine
            if (child.horizontalSizing().get().isExpand) expands += column
            else sizes[column] = max(sizes[column], child.fullSize().width)
        }

        extracted(expands, sizes, available)
        columnSizes = sizes
    }

    private fun extracted(expands: Set<Int>, sizes: IntArray, available: Int) {
        var available1 = available
        sizes.forEach { available1 -= it }
        val validExpands = expands.filter { sizes[it] == 0 }
        if (validExpands.isEmpty()) return
        val shared = available1 / validExpands.size
        for (it in validExpands) sizes[it] = shared
    }

    private inline fun combine(w: Int, h: Int, action: (Int, Int) -> Unit) {
        for (i in 0..<w) for (j in 0..<h) action(i, j)
    }
}