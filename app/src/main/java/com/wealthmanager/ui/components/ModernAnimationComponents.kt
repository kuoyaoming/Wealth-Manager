package com.wealthmanager.ui.components

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable

/**
 * Provides modern, system-optimized animation specifications.
 */
object ModernAnimationSpecs {

    private const val DEFAULT_ANIMATION_DURATION = 300 // ms

    /**
     * Provides a system-optimized tween animation spec.
     *
     * @param duration The duration of the animation in milliseconds. If not provided,
     * a sensible default is used.
     */
    @Composable
    fun getOptimizedAnimationSpec(
        duration: Int = DEFAULT_ANIMATION_DURATION,
    ): AnimationSpec<Float> {
        // In a more advanced implementation, this could query a CompositionLocal
        // provided at the top level of the app to get frame rate info.
        return tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing, // Material Design recommended easing
        )
    }

    /**
     * Provides an optimized spring animation spec for bouncy, natural-feeling animations.
     */
    fun getOptimizedSpringSpec(): SpringSpec<Float> {
        return spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        )
    }
}
