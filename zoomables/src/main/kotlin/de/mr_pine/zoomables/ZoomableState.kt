@file:JvmMultifileClass

package de.mr_pine.zoomables

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.TransformScope
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import de.mr_pine.zoomables.ZoomableState.Rotation.ALWAYS_ENABLED
import de.mr_pine.zoomables.ZoomableState.Rotation.DISABLED
import de.mr_pine.zoomables.ZoomableState.Rotation.LOCK_ROTATION_ON_ZOOM_PAN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val zoomedThreshold = 1.0E-3f

/**
 * An implementation of [TransformableState] containing the values for the current [scale], [offset] and [rotation]. It's normally obtained using [rememberTransformableState]
 * Other than [TransformableState] obtained by [rememberTransformableState], [ZoomableState] exposes [scale], [offset] and [rotation]
 *
 * As
 *
 * @param scale [MutableState]<[Float]> of the scale this state is initialized with
 * @param offset [MutableState]<[Offset]> of the offset this state is initialized with
 * @param rotation [MutableState]<[Float]> in degrees of the rotation this state is initialized with
 *
 * @param onTransformation callback invoked when transformation occurs. The callback receives the
 * change from the previous event. It's relative scale multiplier for zoom, [Offset] in pixels
 * for pan and degrees for rotation.
 *
 * @property scale The current scale as [MutableState]<[Float]>
 * @property offset The current offset as [MutableState]<[Offset]>
 * @property rotation The current rotation in degrees as [MutableState]<[Float]>
 * @property transformed `false` if [scale] is `1`, [offset] is [Offset.Zero] and [rotation] is `0`
 * @property zoomed Whether the content is zoomed (in or out)
 * @property composableCenter The position of the content's center relative to the [Zoomable] Container when [offset] is [Offset.Zero]. Might break when this State is used for multiple [Zoomable]s
 */
public class ZoomableState(
    public val scale: MutableState<Float>,
    public val offset: MutableState<Offset>,
    public val rotation: MutableState<Float>,
    public val rotationBehavior: Rotation,
    onTransformation: ZoomableState.(zoomChange: Float, panChange: Offset, rotationChange: Float) -> Unit
) : TransformableState {

    public val zoomed: Boolean
        get() = scale.value !in (1 - zoomedThreshold)..(1 + zoomedThreshold)

    public val transformed: Boolean
        get() = zoomed || offset.value.getDistanceSquared() !in -1.0E-6f..1.0E-6f || rotation.value !in -1.0E-3f..1.0E-3f

    private val transformScope: TransformScope = object : TransformScope {
        override fun transformBy(zoomChange: Float, panChange: Offset, rotationChange: Float) =
            onTransformation(zoomChange, panChange, rotationChange)
    }

    private val transformMutex = MutatorMutex()

    private val isTransformingState = mutableStateOf(false)

    public var composableCenter: Offset by mutableStateOf(Offset.Zero)


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


    /**
     * Animates a zoom towards the specified position
     * @param zoomChange The zoom factor
     * @param position The position to zoom towards
     * @param currentComposableCenter The current center of the composable relative to the [Zoomable] container. Default might break if this state is used by multiple [Zoomable]s
     */
    public suspend fun animateZoomToPosition(
        zoomChange: Float,
        position: Offset,
        currentComposableCenter: Offset = composableCenter + offset.value,
        animationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow)
    ) {
        val offsetBuffer = offset.value

        val x0 = position.x - currentComposableCenter.x
        val y0 = position.y - currentComposableCenter.y

        val hyp0 = sqrt(x0 * x0 + y0 * y0)
        val hyp1 = zoomChange * hyp0 * (if (x0 > 0) {
            1f
        } else {
            -1f
        })

        val alpha0 = atan(y0 / x0)

        val x1 = cos(alpha0) * hyp1
        val y1 = sin(alpha0) * hyp1

        val transformOffset =
            position - (currentComposableCenter - offsetBuffer) - Offset(x1, y1)

        animateBy(
            zoomChange = zoomChange,
            panChange = transformOffset,
            rotationChange = 0f,
            animationSpec
        )
    }

    /**
     * This enum specifies rotation behaviour for a [ZoomableState]
     *
     * Can be [ALWAYS_ENABLED], [LOCK_ROTATION_ON_ZOOM_PAN] or [DISABLED]
     */
    public enum class Rotation {
        /**
         * Rotating the touch points (your fingers) will always result in rotation of the composable
         */
        ALWAYS_ENABLED,

        /**
         * Rotation is allowed only if touch slop is detected for rotation before pan or zoom motions. If not, pan and zoom gestures will be detected, but rotation gestures will not be.
         */
        LOCK_ROTATION_ON_ZOOM_PAN,

        /**
         * Rotation gestures will not be detected
         */
        DISABLED
    }


    public inner class DefaultDoubleTapBehaviour(
        private val zoomScale: Float = 2f,
        private val animationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow),
        private val coroutineScope: CoroutineScope
    ) : DoubleTapBehaviour {
        override fun onDoubleTap(offset: Offset) {
            if (transformed) {
                coroutineScope.launch {
                    animateBy(
                        zoomChange = 1 / scale.value,
                        panChange = -this@ZoomableState.offset.value,
                        rotationChange = -rotation.value,
                        animationSpec = animationSpec
                    )
                }
            } else {
                coroutineScope.launch {
                    animateZoomToPosition(
                        zoomScale,
                        position = offset,
                        composableCenter,
                        animationSpec = animationSpec
                    )
                }
            }
        }
    }
}

/**
 * @param initialZoom The initial zoom level of this State.
 * @param initialOffset The initial zoom level of this State
 * @param initialRotation The initial zoom level of this State
 *
 * @param rotationBehavior Set how this state handles rotation gestures
 *
 * @param onTransformation callback invoked when transformation occurs. The callback receives the
 * change from the previous event. It's relative scale multiplier for zoom, [Offset] in pixels
 * for pan and degrees for rotation. If not provided the default behaviour is
 * zooming, panning and rotating by the supplied changes. Rotation is kept between positive and negative 180
 *
 * @return A [ZoomableState] initialized with the given [initialZoom], [initialOffset] and [initialRotation]
 */
@Composable
public fun rememberZoomableState(
    initialZoom: Float = 1f,
    initialOffset: Offset = Offset.Zero,
    initialRotation: Float = 0f,
    rotationBehavior: ZoomableState.Rotation = LOCK_ROTATION_ON_ZOOM_PAN,
    onTransformation: ZoomableState.(zoomChange: Float, panChange: Offset, rotationChange: Float) -> Unit = { zoomChange, panChange, rotationChange ->
        scale.value *= zoomChange
        offset.value += panChange
        rotation.value = (rotation.value + rotationChange + 360 + 180) % 360 - 180
    }
): ZoomableState {
    val zoomR = remember { mutableStateOf(initialZoom) }
    val panR = remember { mutableStateOf(initialOffset) }
    val rotationR = remember { mutableStateOf(initialRotation) }
    val lambdaState = rememberUpdatedState(newValue = onTransformation)
    return remember {
        ZoomableState(
            zoomR,
            panR,
            rotationR,
            rotationBehavior,
            lambdaState.value::invoke
        )
    }
}