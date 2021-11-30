package com.primex.extra

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Possible values of [DrawerState].
 */
enum class SideBarValue {
    /**
     * The state of the drawer when it is closed.
     */
    Closed,

    /**
     * The state of the drawer when it is open.
     */
    Open
}

private val SideBarWidth = 72.dp

private val SidebarVelocityThreshold = 400.dp

// TODO: b/177571613 this should be a proper decay settling
// this is taken from the DrawerLayout's DragViewHelper as a min duration.
private val AnimationSpec = TweenSpec<Float>(durationMillis = 256)

private fun calculateFraction(a: Float, b: Float, pos: Float) =
    ((pos - a) / (b - a)).coerceIn(0f, 1f)

/**
 * State of the [ModalDrawer] composable.
 *
 * @param initialValue The initial value of the state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Suppress("NotCloseable")
@OptIn(ExperimentalMaterialApi::class)
@Stable
class SideBarState(
    initialValue: SideBarValue,
    val confirmStateChange: (SideBarValue) -> Boolean = { true }
) {

    val swipeableState = SwipeableState(
        initialValue = initialValue,
        animationSpec = AnimationSpec,
        confirmStateChange = confirmStateChange
    )

    /**
     * Whether the drawer is open.
     */
    val isOpen: Boolean
        get() = currentValue == SideBarValue.Open

    /**
     * Whether the drawer is closed.
     */
    val isClosed: Boolean
        get() = currentValue == SideBarValue.Closed

    /**
     * The current value of the state.
     *
     * If no swipe or animation is in progress, this corresponds to the start the drawer
     * currently in. If a swipe or an animation is in progress, this corresponds the state drawer
     * was in before the swipe or animation started.
     */
    val currentValue: SideBarValue
        get() {
            return swipeableState.currentValue
        }

    /**
     * Whether the state is currently animating.
     */
    val isAnimationRunning: Boolean
        get() {
            return swipeableState.isAnimationRunning
        }

    /**
     * Open the drawer with animation and suspend until it if fully opened or animation has been
     * cancelled. This method will throw [CancellationException] if the animation is
     * interrupted
     *
     * @return the reason the open animation ended
     */
    suspend fun open() = animateTo(SideBarValue.Open, AnimationSpec)

    /**
     * Close the drawer with animation and suspend until it if fully closed or animation has been
     * cancelled. This method will throw [CancellationException] if the animation is
     * interrupted
     *
     * @return the reason the close animation ended
     */
    suspend fun close() = animateTo(SideBarValue.Closed, AnimationSpec)

    /**
     * Set the state of the drawer with specific animation
     *
     * @param targetValue The new value to animate to.
     * @param anim The animation that will be used to animate to the new value.
     */
    @ExperimentalMaterialApi
    suspend fun animateTo(targetValue: SideBarValue, anim: AnimationSpec<Float>) {
        swipeableState.animateTo(targetValue, anim)
    }

    /**
     * Set the state without any animation and suspend until it's set
     *
     * @param targetValue The new target value
     */
    @ExperimentalMaterialApi
    suspend fun snapTo(targetValue: SideBarValue) {
        swipeableState.snapTo(targetValue)
    }

    /**
     * The target value of the drawer state.
     *
     * If a swipe is in progress, this is the value that the Drawer would animate to if the
     * swipe finishes. If an animation is running, this is the target value of that animation.
     * Finally, if no swipe or animation is in progress, this is the same as the [currentValue].
     */
    @Suppress("EXPERIMENTAL_ANNOTATION_ON_WRONG_TARGET")
    @ExperimentalMaterialApi
    @get:ExperimentalMaterialApi
    val targetValue: SideBarValue
        get() = swipeableState.targetValue

    /**
     * The current position (in pixels) of the drawer sheet.
     */
    @Suppress("EXPERIMENTAL_ANNOTATION_ON_WRONG_TARGET")
    @ExperimentalMaterialApi
    @get:ExperimentalMaterialApi
    val offset: State<Float>
        get() = swipeableState.offset

    companion object {
        /**
         * The default [Saver] implementation for [SideBarState].
         */
        fun Saver(confirmStateChange: (SideBarValue) -> Boolean) =
            androidx.compose.runtime.saveable.Saver<SideBarState, SideBarValue>(
                save = { it.currentValue },
                restore = { SideBarState(it, confirmStateChange) }
            )
    }
}

/**
 * Create and [remember] a [SideBarState].
 *
 * @param initialValue The initial value of the state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
fun rememberSideBarState(
    initialValue: SideBarValue,
    confirmStateChange: (SideBarValue) -> Boolean = { true }
): SideBarState {
    return rememberSaveable(saver = SideBarState.Saver(confirmStateChange)) {
        SideBarState(initialValue, confirmStateChange)
    }
}


@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float,
    color: Color
) {
    val closeDrawer = "Close side bar"
    val dismissDrawer = if (open) {
        Modifier
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            .semantics(mergeDescendants = true) {
                contentDescription = closeDrawer
                onClick { onClose(); true }
            }
    } else {
        Modifier
    }

    androidx.compose.foundation.Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(color, alpha = fraction())
    }
}

object SideBarDefaults {

    /**
     * Default Elevation for drawer sheet as specified in material specs
     */
    val Elevation = 16.dp

    val scrimColor: Color
        @Composable
        get() = MaterialTheme.colors.onSurface.copy(alpha = ScrimOpacity)

    /**
     * Default alpha for scrim color
     */
    private const val ScrimOpacity = 0.32f
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SideBar(
    sideBarState: SideBarState = rememberSideBarState(initialValue = SideBarValue.Closed),
    gesturesEnabled: Boolean = true,
    sideBarElevation: Dp = SideBarDefaults.Elevation,
    sideBarBgColor: Color = MaterialTheme.colors.surface,
    sideBarContentColor: Color = contentColorFor(sideBarBgColor),
    scrimColor: Color = SideBarDefaults.scrimColor,
    content: @Composable () -> Unit,
    sideBarContent: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()

    val widthPx = with(LocalDensity.current) { SideBarWidth.toPx() }

    val minValue = -widthPx
    val maxValue = 0f

    val anchors = mapOf(minValue to SideBarValue.Closed, maxValue to SideBarValue.Open)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Box(
        Modifier.swipeable(
            state = sideBarState.swipeableState,
            anchors = anchors,
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Horizontal,
            enabled = gesturesEnabled,
            reverseDirection = isRtl,
            velocityThreshold = SidebarVelocityThreshold,
            resistance = null
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((widthPx + sideBarState.offset.value).roundToInt(), 0) },
        ){
            content()
        }

        Scrim(
            open = sideBarState.isOpen,
            onClose = {
                if (
                    gesturesEnabled
                    && sideBarState.confirmStateChange(SideBarValue.Closed)
                ) {
                    scope.launch { sideBarState.close() }
                }
            },
            fraction = {
                calculateFraction(
                    minValue,
                    maxValue,
                    sideBarState.offset.value
                )
            },
            color = scrimColor
        )

        Surface(
            color = sideBarBgColor,
            contentColor = sideBarContentColor,
            elevation = sideBarElevation,
            modifier = Modifier
                .requiredWidth(width = SideBarWidth)
                .fillMaxHeight()
                .offset { IntOffset(sideBarState.offset.value.roundToInt(), 0) }
                .semantics {
                    paneTitle = "side bar menu"
                    if (sideBarState.isOpen) {
                        dismiss {
                            if (
                                sideBarState.confirmStateChange(SideBarValue.Closed)
                            ) {
                                scope.launch { sideBarState.close() }
                            }; true
                        }
                    }
                },
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .selectableGroup(),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = sideBarContent
            )
        }
    }
}