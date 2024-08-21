package dev.kikugie.kowoui.experimental

fun at(index: Int) = ListPos(index)
fun at(row: Int, column: Int) = GridPos(row, column)

interface Position

data class ListPos internal constructor(@JvmField val index: Int) : Position

data class GridPos internal constructor(@JvmField val row: Int, @JvmField val column: Int) : Position