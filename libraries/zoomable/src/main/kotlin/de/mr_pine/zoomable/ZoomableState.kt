@file:JvmMultifileClass

package de.mr_pine.zoomable

import androidx.compose.animation.core.*
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.TransformScope
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.coroutineScope

/**
 * An implementation of [TransformableState] containing the values for the current [scale], [offset] and [rotation]. It's normally obtained using [rememberTransformableState]
 * Other than [TransformableState] obtained by [rememberTransformableState], [ZoomableState] exposes [scale], [offset] and [rotation]
 *
 * @param scale [MutableState]<[Float]> of the scale this state is initialized with
 * @param offset [MutableState]<[Offset]> of the offset this state is initialized with
 * @param rotation [MutableState]<[Float]> in degrees of the rotation this state is initialized with
 *
 * @property scale The current scale as [MutableState]<[Float]>
 * @property offset The current offset as [MutableState]<[Offset]>
 * @property rotation The current rotation in degrees as [MutableState]<[Float]>
 */
public class ZoomableState(
    public var scale: MutableState<Float>,
    public var offset: MutableState<Offset>,
    public var rotation: MutableState<Float>,
    onTransformation: ((zoomChange: Float, panChange: Offset, rotationChange: Float) -> Unit)? = null
) : TransformableState {
    private val transformScope: TransformScope = object : TransformScope {
        override fun transformBy(zoomChange: Float, panChange: Offset, rotationChange: Float) =
            if (onTransformation != null) {
                onTransformation.invoke(zoomChange, panChange, rotationChange)
            } else {
                scale.value *= zoomChange
                offset.value += panChange
                rotation.value += rotationChange
            }
    }

    private val transformMutex = MutatorMutex()

    private val isTransformingState = mutableStateOf(false)

    override suspend fun transform(
        transformPriority: MutatePriority,
        block: suspend TransformScope.() -> Unit
    ): Unit = coroutineScope {
        transformMutex.mutateWith(transformScope, transformPriority) {
            isTransformingState.value = true
            try {
                block()
            } finally {
                isTransformingState.value = false
            }
        }
    }

    override val isTransformInProgress: Boolean
        get() = isTransformingState.value

    public suspend fun animateBy(
        zoomChange: Float, panChange: Offset, rotationChange: Float,
        animationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow)
    ) {
        val baseScale = scale.value
        var previous = 0f
        transform {
            AnimationState(initialValue = previous).animateTo(1f, animationSpec) {
                val delta = this.value - previous
                previous = this.value
                transformBy(
                    zoomChange = (baseScale * (1 + (zoomChange - 1) * this.value)) / scale.value,
                    panChange = panChange * delta,
                    rotationChange = delta * rotationChange
                )
            }
        }
    }
}

/**
 * @return A [ZoomableState] initialized with the given [zoom], [offset] and [rotation]
 */
@Composable
public fun rememberZoomableState(
    zoom: Float,
    offset: Offset,
    rotation: Float,
    onTransformation: ((zoomChange: Float, panChange: Offset, rotationChange: Float) -> Unit)? = null
): ZoomableState {
    val zoomR = remember { mutableStateOf(zoom) }
    val panR = remember { mutableStateOf(offset) }
    val rotationR = remember { mutableStateOf(rotation) }
    val lambdaState = rememberUpdatedState(newValue = onTransformation)
    return remember {
        ZoomableState(
            zoomR,
            panR,
            rotationR,
            if (lambdaState.value != null) {
                { z, p, r ->
                    lambdaState.value?.invoke(
                        z,
                        p,
                        r
                    )
                }
            } else null
        )
    }
}

/**
 * @return A [ZoomableState] initialized with scale = 1, offset = [Offset.Zero] and rotation = 0
 */
@Composable
public fun rememberZoomableState(): ZoomableState {
    return rememberZoomableState(zoom = 1f, offset = Offset.Zero, rotation = 0f)
}