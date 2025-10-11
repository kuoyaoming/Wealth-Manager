package com.wealthmanager.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.wealthmanager.ui.performance.ModernFrameRateManager
import kotlinx.coroutines.delay

/**
 * Modern animation components using system-optimized frame rates
 * Replaces custom HighRefreshRateComponents with Android guideline-compliant implementations
 */
object ModernAnimationSpecs {
    /**
     * Get system-optimized animation specs based on current frame rate
     */
    fun getOptimizedAnimationSpec(
        frameRateManager: ModernFrameRateManager,
        duration: Int? = null,
    ): AnimationSpec<Float> {
        val actualDuration = duration ?: frameRateManager.getRecommendedAnimationDuration()

        return tween<Float>(
            durationMillis = actualDuration,
            easing = FastOutSlowInEasing, // Material Design recommended easing
        )
    }

    /**
     * Get optimized spring animation
     */
    fun getOptimizedSpringSpec(): SpringSpec<Float> {
        return spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        )
    }

    /**
     * Get optimized repeatable animation for loading states
     */
    fun getOptimizedRepeatableSpec(frameRateManager: ModernFrameRateManager): AnimationSpec<Float> {
        return infiniteRepeatable<Float>(
            animation =
                tween(
                    durationMillis = frameRateManager.getRecommendedAnimationDuration(),
                    easing = LinearEasing,
                ),
            repeatMode = RepeatMode.Restart,
        )
    }
}

/**
 * Modern button component with system-optimized animations
 */
@Composable
fun ModernAnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    frameRateManager: ModernFrameRateManager,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = ModernAnimationSpecs.getOptimizedSpringSpec(),
        label = "button_scale",
    )

    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1.0f else 0.6f,
        animationSpec = ModernAnimationSpecs.getOptimizedAnimationSpec(frameRateManager),
        label = "button_alpha",
    )

    Button(
        onClick = onClick,
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
 * Modern card component with system-optimized animations
 */
@Composable
fun ModernAnimatedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    frameRateManager: ModernFrameRateManager,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 4f,
        animationSpec = ModernAnimationSpecs.getOptimizedAnimationSpec(frameRateManager),
        label = "card_elevation",
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = ModernAnimationSpecs.getOptimizedSpringSpec(),
        label = "card_scale",
    )

    Card(
        onClick = onClick,
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
 * Modern progress indicator with system-optimized animations
 */
@Composable
fun ModernAnimatedProgressIndicator(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    frameRateManager: ModernFrameRateManager,
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = ModernAnimationSpecs.getOptimizedAnimationSpec(frameRateManager),
        label = "progress_alpha",
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.8f,
        animationSpec = ModernAnimationSpecs.getOptimizedSpringSpec(),
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
 * Modern loading animation with system-optimized rotation
 */
@Composable
fun ModernLoadingAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    frameRateManager: ModernFrameRateManager,
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = ModernAnimationSpecs.getOptimizedAnimationSpec(frameRateManager),
        label = "loading_alpha",
    )

    val rotation by animateFloatAsState(
        targetValue = if (isVisible) 360f else 0f,
        animationSpec = ModernAnimationSpecs.getOptimizedRepeatableSpec(frameRateManager),
        label = "loading_rotation",
    )

    if (alpha > 0.01f) {
        CircularProgressIndicator(
            modifier =
                modifier
                    .graphicsLayer {
                        this.alpha = alpha
                        rotationZ = rotation
                    },
        )
    }
}

/**
 * Modern fade transition with system-optimized timing
 */
@Composable
fun <T> ModernAnimatedVisibility(
    targetState: T,
    frameRateManager: ModernFrameRateManager,
    content: @Composable (T) -> Unit,
): T {
    var currentState by remember { mutableStateOf(targetState) }

    LaunchedEffect(targetState) {
        if (targetState != currentState) {
            // Use system-optimized delay
            delay(frameRateManager.getRecommendedAnimationDuration().toLong())
            currentState = targetState
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (currentState == targetState) 1.0f else 0.0f,
        animationSpec = ModernAnimationSpecs.getOptimizedAnimationSpec(frameRateManager),
        label = "visibility_alpha",
    )

    if (alpha > 0.01f) {
        content(currentState)
    }

    return currentState
}
