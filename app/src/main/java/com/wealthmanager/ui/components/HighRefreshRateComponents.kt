package com.wealthmanager.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import kotlinx.coroutines.delay

/**
 * 120Hz optimized animation configuration
 */
object HighRefreshRateAnimations {
    // 120Hz optimized animation specifications
    val FastEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val MediumEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val SlowEasing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)

    // Animation durations for 120Hz
    const val FAST_DURATION = 150 // milliseconds
    const val MEDIUM_DURATION = 300 // milliseconds
    const val SLOW_DURATION = 500 // milliseconds

    /**
     * 120Hz optimized spring animation
     */
    fun <T> smoothSpring() =
        spring<T>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
            visibilityThreshold = null,
        )

    /**
     * 120Hz optimized tween animation
     */
    fun fastTween(durationMillis: Int = FAST_DURATION) =
        tween<Float>(
            durationMillis = durationMillis,
            easing = FastEasing,
        )

    /**
     * 120Hz optimized medium speed animation
     */
    fun mediumTween(durationMillis: Int = MEDIUM_DURATION) =
        tween<Float>(
            durationMillis = durationMillis,
            easing = MediumEasing,
        )

    /**
     * 120Hz optimized slow animation
     */
    fun slowTween(durationMillis: Int = SLOW_DURATION) =
        tween<Float>(
            durationMillis = durationMillis,
            easing = SlowEasing,
        )
}

/**
 * High refresh rate optimized touch feedback
 */
@Composable
fun Modifier.highRefreshRateClickable(onClick: () -> Unit): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = HighRefreshRateAnimations.smoothSpring(),
        label = "click_scale",
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { 
                    isPressed = true
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                },
                onDragEnd = {
                    isPressed = false
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                    onClick()
                },
            ) { _, _ -> }
        }
}

/**
 * 120Hz optimized button component
 */
@Composable
fun HighRefreshRateButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = HighRefreshRateAnimations.smoothSpring(),
        label = "button_scale",
    )

    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1.0f else 0.6f,
        animationSpec = HighRefreshRateAnimations.fastTween(),
        label = "button_alpha",
    )

    Button(
        onClick = {
            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
            onClick()
        },
        modifier =
            modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
        enabled = enabled,
        interactionSource = interactionSource,
    ) {
        content()
    }
}

/**
 * 120Hz optimized card component
 */
@Composable
fun HighRefreshRateCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 4f,
        animationSpec = HighRefreshRateAnimations.fastTween(),
        label = "card_elevation",
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = HighRefreshRateAnimations.smoothSpring(),
        label = "card_scale",
    )

    Card(
        onClick = {
            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
            onClick()
        },
        modifier =
            modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        interactionSource = interactionSource,
    ) {
        content()
    }
}

/**
 * 120Hz optimized list item component
 */
@Composable
fun HighRefreshRateListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue =
            if (isPressed) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            },
        animationSpec =
            tween<Color>(
                durationMillis = HighRefreshRateAnimations.FAST_DURATION,
                easing = HighRefreshRateAnimations.FastEasing,
            ),
        label = "list_item_background",
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.99f else 1.0f,
        animationSpec = HighRefreshRateAnimations.smoothSpring(),
        label = "list_item_scale",
    )

    Surface(
        onClick = onClick,
        modifier =
            modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        color = backgroundColor,
        interactionSource = interactionSource,
    ) {
        content()
    }
}

/**
 * 120Hz optimized progress indicator
 */
@Composable
fun HighRefreshRateProgressIndicator(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = HighRefreshRateAnimations.fastTween(),
        label = "progress_alpha",
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.8f,
        animationSpec = HighRefreshRateAnimations.smoothSpring(),
        label = "progress_scale",
    )

    if (alpha > 0.01f) {
        CircularProgressIndicator(
            modifier =
                modifier
                    .graphicsLayer {
                        this.alpha = alpha
                        scaleX = scale
                        scaleY = scale
                    },
        )
    }
}

/**
 * 120Hz optimized fade in/out animation
 */
@Composable
fun <T> rememberHighRefreshRateAnimatedVisibility(
    targetState: T,
    content: @Composable (T) -> Unit,
): T {
    var currentState by remember { mutableStateOf(targetState) }

    LaunchedEffect(targetState) {
        if (targetState != currentState) {
            delay(50) // Brief delay to ensure smooth transition
            currentState = targetState
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (currentState == targetState) 1.0f else 0.0f,
        animationSpec = HighRefreshRateAnimations.fastTween(),
        label = "visibility_alpha",
    )

    if (alpha > 0.01f) {
        content(currentState)
    }

    return currentState
}

/**
 * 120Hz optimized scroll animation
 */
@Composable
fun rememberHighRefreshRateScrollState(): LazyListState {
    return rememberLazyListState()
}

/**
 * 120Hz optimized page transition animation
 */
@Composable
fun <T> rememberHighRefreshRatePageTransition(
    targetPage: T,
    content: @Composable (T) -> Unit,
) {
    var currentPage by remember { mutableStateOf(targetPage) }

    LaunchedEffect(targetPage) {
        if (targetPage != currentPage) {
            delay(100) // Page transition delay
            currentPage = targetPage
        }
    }

    // val offsetX by animateFloatAsState(
    //     targetValue = if (currentPage == targetPage) 0f else 300f,
    //     animationSpec = HighRefreshRateAnimations.mediumTween(),
    //     label = "page_offset"
    // )

    // val alpha by animateFloatAsState(
    //     targetValue = if (currentPage == targetPage) 1.0f else 0.0f,
    //     animationSpec = HighRefreshRateAnimations.fastTween(),
    //     label = "page_alpha"
    // )

    content(currentPage)
}
