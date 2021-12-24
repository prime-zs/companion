package com.primex.extra

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val TAG = "Modifiers"

private val DEFAULT_FADING_EDGE_LENGTH = 10.dp


/**
 * A fading edge [Modifier].
 * Defines which edges(Vertical/Horizontal) should be faded on scrolling.
 *
 * @param color: The [Color] to use, default value [isLight] -> [Color.White] else [Color.Black]
 * @param state: The [ScrollState] of the composable. Supports [LazyListState], [ScrollState] of both [Columns] & [Rows].
 * @param horizontal: if [direction] is vertical pass [false].
 * @param length: The length to which to fade in [Dp]. Default [DEFAULT_FADING_EDGE_LENGTH]
 *
 *  *Note:* In case of [ScrollState] i.e, [Rows] and column, use this modifier before [verticalScroll] method.
 */
fun Modifier.fadeEdge(
    state: ScrollableState,
    color: Color? = null,
    horizontal: Boolean = true,
    length: Dp = DEFAULT_FADING_EDGE_LENGTH,
) = composed(
    debugInspectorInfo {
        name = "length"
        value = length
    },
) {
    val color = color ?: (if (isLight()) Color.White else Color.Black)
    Modifier.drawWithContent {
        // The length in pixels
        val lengthPx = length.toPx()

        // The alpha of color

        // it constitutes of two reacts top/bottom or left right.
        // animate until scroll is less than lengthPx to 1
        // if offset of scroll(in Px) is > 0 show first.
        val alpha1: Float

        // animate until scroll is greater than max - lengthPx to 0
        // if offset of scroll(in Px) is less than maxHeight - lengthpx of list.
        val alpha2: Float

        fun alpha(fraction: Float) =
            lerp(0f, 1f, fraction.coerceAtMost(1f))

        // determine value of alpha 1 and alpha2 from state.
        when (state) {
            is ScrollState -> {
                // calculate fraction value between 0 and 1
                // since value moves from 0 to max [i.e. size of the list in pixels]
                // we just need to animate for first lengthPx pixels for rest it is visible.
                var fraction = state.value / lengthPx
                alpha1 = if (state.value > lengthPx) 1f else alpha(fraction = fraction)

                // invert fraction from last item
                // animate for last lengthPx pixels but inverted.
                fraction = 1 - (state.maxValue - state.value) / lengthPx
                alpha2 = if (state.value < (state.maxValue - lengthPx)) 1f else 1 - alpha(fraction)
            }
            is LazyListState -> {
                val info = state.layoutInfo

                //  we need to animate for oth item till lengthPx Pixels
                val fraction = state.firstVisibleItemScrollOffset / lengthPx
                alpha1 = if (state.firstVisibleItemIndex != 0) 1f else alpha(fraction)

                val isLast =
                    state.firstVisibleItemIndex + info.visibleItemsInfo.size == info.totalItemsCount
                //FixMe Here is a bug which causes blink as we cant determine actual last item
                // animate for
                alpha2 = if (!isLast) 1f else 1 - alpha(fraction)
            }
            else -> error("The $state is not supported.")
        }

        fun drawVertical() {
            // draw top edge
            val color1 = color.copy(alpha1)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(color1, Color.Transparent),
                    startY = 0f,
                    endY = lengthPx,
                ),
                size = Size(
                    this.size.width,
                    lengthPx
                ),
            )
            //draw bottom edge
            val color2 = color.copy(alpha2)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, color2),
                    startY = size.height - lengthPx,
                    endY = size.height,
                ),
                topLeft = Offset(x = 0f, y = size.height - lengthPx),
            )
        }

        fun drawHorizontal() {
            // start edge
            val color1 = color.copy(alpha1)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(color1, Color.Transparent),
                    startX = 0f,
                    endX = lengthPx,
                ),
                size = Size(
                    lengthPx,
                    this.size.height,
                ),
            )

            //end edge.
            val color2 = color.copy(alpha2)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, color2),
                    startX = size.width - lengthPx,
                    endX = size.width,
                ),
                topLeft = Offset(x = size.width - lengthPx, y = 0f),
            )
        }

        drawContent()

        when (horizontal) {
            true -> drawHorizontal()
            else -> drawVertical()
        }
    }
}


/**
 * @see fadeEdge
 */
fun Modifier.verticalFadingEdge(
    state: ScrollableState,
    color: Color? = null,
    length: Dp = DEFAULT_FADING_EDGE_LENGTH,
) = fadeEdge(state = state, horizontal = false, color = color, length = length)


/**
 * @see fadeEdge
 */
fun Modifier.horizontalFadingEdge(
    state: ScrollableState,
    color: Color? = null,
    length: Dp = DEFAULT_FADING_EDGE_LENGTH,
) = fadeEdge(state = state, horizontal = true, color = color, length = length)

/**
 * An shortcut for implementing [animateContentSize] with [tween]
 *  @param durationMillis duration of the animation spec
 */
fun Modifier.animate(durationMillis: Int = Anim.durationMedium) =
    animateContentSize(animationSpec = tween(durationMillis))

private fun Modifier.gradient(
    colors: List<Color>,
    brushProvider: (List<Color>, Size) -> Brush
): Modifier = composed {
    var size by remember { mutableStateOf(Size.Zero) }
    val gradient = remember(colors, size) { brushProvider(colors, size) }
    drawWithContent {
        size = this.size
        drawContent()
        drawRect(brush = gradient)
    }
}

/**
 * Adds [gradient] over content.
 * [colors] determine top -> bottom or bottom -> top orientation.
 *
 * Note: default is with [Color.Black] and bottom -> top.
 */
fun Modifier.verticalGradient(
    colors: List<Color> = listOf(
        Color.Transparent,
        Color.Black,
    )
) = gradient(colors) { gradientColors, size ->
    Brush.verticalGradient(
        colors = gradientColors,
        startY = 0f,
        endY = size.height
    )
}

/**
 * @see [verticalGradient]
 */
fun Modifier.horizontalGradient(
    colors: List<Color> = listOf(
        Color.Black,
        Color.Transparent
    )
) = gradient(colors) { gradientColors, size ->
    Brush.horizontalGradient(
        colors = gradientColors,
        startX = 0f,
        endX = size.width
    )
}


/**
 * @see [Brush.radialGradient]
 */
fun Modifier.radialGradient(
    colors: List<Color> = listOf(
        Color.Transparent,
        Color.Black
    ),
    center: Offset = Offset.Unspecified,
    radius: Float = Float.POSITIVE_INFINITY,
    tileMode: TileMode = TileMode.Clamp
): Modifier = gradient(colors) { gradientColors, size ->
    Brush.radialGradient(
        colors = gradientColors,
        center = center,
        radius = radius,
        tileMode = tileMode
    )
}


/**
 * This modifier acquires focus to this widget as soon as the user clicks.
 */
fun Modifier.acquireFocusOnInteraction(
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null
): Modifier = composed {
    val interaction = interactionSource ?: remember {
        MutableInteractionSource()
    }
    val requester = remember {
        FocusRequester()
    }
    val isFocused by interaction.collectIsFocusedAsState()
    Modifier
        .focusRequester(requester)
        .focusable(true, interactionSource = interaction)
        .clickable(
            enabled = !isFocused,
            indication = indication,
            onClick = { requester.requestFocus() },
            interactionSource = remember {
                MutableInteractionSource()
            }
        )
        .then(this)
}
