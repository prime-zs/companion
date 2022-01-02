package com.primex.extra

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/**
 * A short-cut name for [MaterialTheme]
 */
typealias Material = MaterialTheme

/**
 * A function meant to accompany composable without triggering whole composable recomposition
 */
@SuppressLint("ComposableNaming")
@Composable
@ReadOnlyComposable
fun calculate(calculation: () -> Unit) {
    calculation.invoke()
}

val Material.isLight
    @Composable
    get() = colors.isLight

/**
 * The Height of the mobile display device
 */
val Density.displayHeight: Dp
    get() = Resources.getSystem().displayMetrics.heightPixels.toDp()

/**
 * The width of the mobile display device
 */
val Density.displayWidth: Dp
    get() = Resources.getSystem().displayMetrics.widthPixels.toDp()


inline fun <reified T> castTo(anything: Any): T {
    return anything as T
}


data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val forth: D
) {

    /**
     * Returns string representation of the [Quad] including its [first], [second], [third], and [forth] values.
     */
    override fun toString(): String = "($first, $second, $third, $forth)"
}

