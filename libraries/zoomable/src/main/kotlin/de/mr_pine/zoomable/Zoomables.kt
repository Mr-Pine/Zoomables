@file:JvmMultifileClass

package de.mr_pine.zoomable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*

private const val TAG = "ZoomImage"

/*@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalFoundationApi
@Composable
public fun ZoomableImage(
    painter: Painter,
    onSwipeLeft: () -> Unit = { Log.d(TAG, "ZoomableImage: swipe left") },
    onSwipeRight: () -> Unit = { Log.d(TAG, "ZoomableImage: swipe right") }
) {
    val scope = rememberCoroutineScope()

    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        Log.d(TAG, "transformBy: hi, zoom: $zoomChange, pan: $offsetChange, rot: $rotationChange")
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var imageCenter by remember { mutableStateOf(Offset.Zero) }
    var transformOffset by remember { mutableStateOf(Offset.Zero) }

    fun onTransformGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        transformRotation: Float
    ) {
        offset += pan
        scale *= zoom
        rotation += transformRotation

        val x0 = centroid.x - imageCenter.x
        val y0 = centroid.y - imageCenter.y

        val hyp0 = sqrt(x0 * x0 + y0 * y0)
        val hyp1 = zoom * hyp0 * (if (x0 > 0) {
            1f
        } else {
            -1f
        })

        val alpha0 = atan(y0 / x0)

        val alpha1 = alpha0 + (transformRotation * ((2 * PI) / 360))

        val x1 = cos(alpha1) * hyp1
        val y1 = sin(alpha1) * hyp1

        transformOffset =
            centroid - (imageCenter - offset) - Offset(x1.toFloat(), y1.toFloat())
        offset = transformOffset
    }

    Box(
        Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale != 1f) {
                            scope.launch {
                                state.animateZoomBy(1 / scale)
                            }
                            offset = Offset.Zero
                            rotation = 0f
                        } else {
                            scope.launch {
                                state.animateZoomBy(2f)
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                val panZoomLock = true
                forEachGesture {
                    awaitPointerEventScope {
                        var transformRotation = 0f
                        var zoom = 1f
                        var pan = Offset.Zero
                        var pastTouchSlop = false
                        val touchSlop = viewConfiguration.touchSlop
                        var lockedToPanZoom = false
                        var drag: PointerInputChange?
                        var overSlop = Offset.Zero

                        val down = awaitFirstDown(requireUnconsumed = false)


                        var transformEventCounter = 0
                        do {
                            val event = awaitPointerEvent()
                            val canceled = event.changes.fastAny { it.positionChangeConsumed() }
                            var relevant = true
                            if (event.changes.size > 1) {
                                if (!canceled) {
                                    val zoomChange = event.calculateZoom()
                                    val rotationChange = event.calculateRotation()
                                    val panChange = event.calculatePan()

                                    if (!pastTouchSlop) {
                                        zoom *= zoomChange
                                        transformRotation += rotationChange
                                        pan += panChange

                                        val centroidSize =
                                            event.calculateCentroidSize(useCurrent = false)
                                        val zoomMotion = abs(1 - zoom) * centroidSize
                                        val rotationMotion =
                                            abs(transformRotation * PI.toFloat() * centroidSize / 180f)
                                        val panMotion = pan.getDistance()

                                        if (zoomMotion > touchSlop ||
                                            rotationMotion > touchSlop ||
                                            panMotion > touchSlop
                                        ) {
                                            pastTouchSlop = true
                                            lockedToPanZoom =
                                                panZoomLock && rotationMotion < touchSlop
                                        }
                                    }

                                    if (pastTouchSlop) {
                                        val eventCentroid =
                                            event.calculateCentroid(useCurrent = false)
                                        val effectiveRotation =
                                            if (lockedToPanZoom) 0f else rotationChange
                                        if (effectiveRotation != 0f ||
                                            zoomChange != 1f ||
                                            panChange != Offset.Zero
                                        ) {
                                            onTransformGesture(
                                                eventCentroid,
                                                panChange,
                                                zoomChange,
                                                effectiveRotation
                                            )
                                        }
                                        event.changes.fastForEach {
                                            if (it.positionChanged()) {
                                                it.consumeAllChanges()
                                            }
                                        }
                                    }
                                }
                            } else if (transformEventCounter > 3) relevant = false
                            transformEventCounter++
                        } while (!canceled && event.changes.fastAny { it.pressed } && relevant)

                        do {
                            val event = awaitPointerEvent()
                            drag = awaitTouchSlopOrCancellation(down.id) { change, over ->
                                change.consumePositionChange()
                                Log.d(
                                    TAG,
                                    "ZoomableImage: over: $over, scrollDelta: ${change.scrollDelta}"
                                )
                                overSlop = over
                            }
                        } while (drag != null && !drag.positionChangeConsumed())
                        if (drag != null) {
                            dragOffset = Offset.Zero
                            if (scale !in 0.92f..1.08f) {
                                offset += overSlop
                            } else {
                                dragOffset += overSlop
                            }
                            if (drag(drag.id) {
                                    Log.d(TAG, "ZoomableImage: onDrag")
                                    if (scale !in 0.92f..1.08f) {
                                        offset += it.positionChange()
                                    } else {
                                        dragOffset += it.positionChange()
                                    }
                                    it.consumePositionChange()
                                }
                            ) {
                                if (scale in 0.92f..1.08f) {
                                    val offsetX = dragOffset.x
                                    if (offsetX > 300) {
                                        onSwipeRight()

                                    } else if (offsetX < -300) {
                                        onSwipeLeft()
                                    }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        Image(
            painter = painter,
            contentDescription = "Comic Image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RectangleShape)
                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                .graphicsLayer(
                    scaleX = scale - 0.02f,
                    scaleY = scale - 0.02f,
                    rotationZ = rotation
                )
                .onGloballyPositioned { coordinates ->
                    val localOffset =
                        Offset(
                            coordinates.size.width.toFloat() / 2,
                            coordinates.size.height.toFloat() / 2
                        )
                    val windowOffset = coordinates.localToWindow(localOffset)
                    imageCenter = coordinates.parentLayoutCoordinates?.windowToLocal(windowOffset)
                        ?: Offset.Zero
                },
            contentScale = ContentScale.Fit
        )
    }
}*/

/**
 * Creates a composable that wraps a given [Composable] and adds zoom, pan, rotation, double tap and swipe functionality
 *
 *
 * **NOTE** this Composable's functionality is different from using [Modifier.transformable] in multiple ways:
 * * Nicer zooming behaviour by zooming away from the center of the multitouch (instead of the [Composable] center)
 * * Nicer rotation behaviour by rotating around the center of the multitouch (instead of the [Composable] center)
 * * Ability to use swipe gestures when not zoomed in and panning when zoomed in
 * * Provides simple functions for swipe and tap events
 * * Wrapping most of the boilerplate code and providing even simpler functions for images with [ZoomableImage] and [EasyZoomableImage]
 *
 * @param coroutineScope used for smooth asynchronous zoom/pan/rotation animations
 * @param zoomableState Contains the current transform states - obtained via [rememberZoomableState]
 * @param onSwipeLeft Optional function to run when user swipes from right to left - does nothing by default
 * @param onSwipeRight Optional function to run when user swipes from left to right - does nothing by default
 * @param onDoubleTap Optional function to run when user double taps. Zooms in by 2x when scale is currently 1 and zooms out to scale = 1 when zoomed in by default
 */
@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalFoundationApi
@Composable
public fun Zoomable(
    coroutineScope: CoroutineScope,
    zoomableState: ZoomableState,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    minimumSwipeDistance: Int = 0,
    onDoubleTap: (Offset) -> Unit = {
        if (zoomableState.scale.value != 1f) {
            coroutineScope.launch {
                zoomableState.animateBy(
                    zoomChange = 1 / zoomableState.scale.value,
                    panChange = -zoomableState.offset.value,
                    rotationChange = -zoomableState.rotation.value
                )
            }
        } else {
            coroutineScope.launch {
                zoomableState.animateZoomBy(2f)
            }
        }
    },
    Content: @Composable (BoxScope.() -> Unit),
) {

    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var composableCenter by remember { mutableStateOf(Offset.Zero) }
    var transformOffset by remember { mutableStateOf(Offset.Zero) }

    fun onTransformGesture(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        transformRotation: Float
    ) {

        var tempOffset = zoomableState.offset.value + pan

        val x0 = centroid.x - composableCenter.x
        val y0 = centroid.y - composableCenter.y

        val hyp0 = sqrt(x0 * x0 + y0 * y0)
        val hyp1 = zoom * hyp0 * (if (x0 > 0) {
            1f
        } else {
            -1f
        })

        val alpha0 = atan(y0 / x0)

        val alpha1 = alpha0 + (transformRotation * ((2 * PI) / 360))

        val x1 = cos(alpha1) * hyp1
        val y1 = sin(alpha1) * hyp1

        transformOffset =
            centroid - (composableCenter - tempOffset) - Offset(x1.toFloat(), y1.toFloat())
        tempOffset = transformOffset

        coroutineScope.launch {
            zoomableState.transform {
                transformBy(
                    zoomChange = zoom,
                    panChange = tempOffset - zoomableState.offset.value,
                    rotationChange = transformRotation
                )
            }
        }

    }

    Box(
        Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = onDoubleTap
                )
            }
            .pointerInput(Unit) {
                val panZoomLock = true
                forEachGesture {
                    awaitPointerEventScope {
                        var transformRotation = 0f
                        var zoom = 1f
                        var pan = Offset.Zero
                        var pastTouchSlop = false
                        val touchSlop = viewConfiguration.touchSlop
                        var lockedToPanZoom = false
                        var drag: PointerInputChange?
                        var overSlop = Offset.Zero

                        val down = awaitFirstDown(requireUnconsumed = false)


                        var transformEventCounter = 0
                        do {
                            val event = awaitPointerEvent()
                            val canceled = event.changes.fastAny { it.positionChangeConsumed() }
                            var relevant = true
                            if (event.changes.size > 1) {
                                if (!canceled) {
                                    val zoomChange = event.calculateZoom()
                                    val rotationChange = event.calculateRotation()
                                    val panChange = event.calculatePan()

                                    if (!pastTouchSlop) {
                                        zoom *= zoomChange
                                        transformRotation += rotationChange
                                        pan += panChange

                                        val centroidSize =
                                            event.calculateCentroidSize(useCurrent = false)
                                        val zoomMotion = abs(1 - zoom) * centroidSize
                                        val rotationMotion =
                                            abs(transformRotation * PI.toFloat() * centroidSize / 180f)
                                        val panMotion = pan.getDistance()

                                        if (zoomMotion > touchSlop ||
                                            rotationMotion > touchSlop ||
                                            panMotion > touchSlop
                                        ) {
                                            pastTouchSlop = true
                                            lockedToPanZoom =
                                                panZoomLock && rotationMotion < touchSlop
                                        }
                                    }

                                    if (pastTouchSlop) {
                                        val eventCentroid =
                                            event.calculateCentroid(useCurrent = false)
                                        val effectiveRotation =
                                            if (lockedToPanZoom) 0f else rotationChange
                                        if (effectiveRotation != 0f ||
                                            zoomChange != 1f ||
                                            panChange != Offset.Zero
                                        ) {
                                            onTransformGesture(
                                                eventCentroid,
                                                panChange,
                                                zoomChange,
                                                effectiveRotation
                                            )
                                        }
                                        event.changes.fastForEach {
                                            if (it.positionChanged()) {
                                                it.consumeAllChanges()
                                            }
                                        }
                                    }
                                }
                            } else if (transformEventCounter > 3) relevant = false
                            transformEventCounter++
                        } while (!canceled && event.changes.fastAny { it.pressed } && relevant)

                        do {
                            val event = awaitPointerEvent()
                            drag = awaitTouchSlopOrCancellation(down.id) { change, over ->
                                change.consumePositionChange()
                                overSlop = over
                            }
                        } while (drag != null && !drag.positionChangeConsumed())
                        if (drag != null) {
                            dragOffset = Offset.Zero
                            if (zoomableState.scale.value !in 0.92f..1.08f) {
                                coroutineScope.launch {
                                    zoomableState.transform {
                                        transformBy(1f, overSlop, 0f)
                                    }
                                }
                            } else {
                                dragOffset += overSlop
                            }
                            if (drag(drag.id) {
                                    if (zoomableState.scale.value !in 0.92f..1.08f) {
                                        zoomableState.offset.value += it.positionChange()
                                    } else {
                                        dragOffset += it.positionChange()
                                    }
                                    it.consumePositionChange()
                                }
                            ) {
                                if (zoomableState.scale.value in 0.92f..1.08f) {
                                    val offsetX = dragOffset.x
                                    if (offsetX > minimumSwipeDistance) {
                                        onSwipeRight()

                                    } else if (offsetX < -minimumSwipeDistance) {
                                        onSwipeLeft()
                                    }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .clip(RectangleShape)
                .offset {
                    IntOffset(
                        zoomableState.offset.value.x.roundToInt(),
                        zoomableState.offset.value.y.roundToInt()
                    )
                }
                .graphicsLayer(
                    scaleX = zoomableState.scale.value,
                    scaleY = zoomableState.scale.value,
                    rotationZ = zoomableState.rotation.value
                )
                .onGloballyPositioned { coordinates ->
                    val localOffset =
                        Offset(
                            coordinates.size.width.toFloat() / 2,
                            coordinates.size.height.toFloat() / 2
                        )
                    val windowOffset = coordinates.localToWindow(localOffset)
                    composableCenter =
                        coordinates.parentLayoutCoordinates?.windowToLocal(windowOffset)
                            ?: Offset.Zero
                },
        ) {
            Content()
        }
    }
}