@file:JvmMultifileClass

package de.mr_pine.zoomables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.CoroutineScope


/**
 * Creates a composable that shows an [ImageBitmap] and behaves like [Zoomable]
 *
 * @param coroutineScope used for smooth asynchronous zoom/pan/rotation animations
 * @param zoomableState Contains the current transform states - obtained via [rememberZoomableState]
 * @param bitmap The [ImageBitmap] to draw
 * @param modifier Modifier applied to the underlying [Image] Composable
 * @param contentDescription text for accessibility see [Image] for further info
 * @param onSwipeLeft Optional function to run when user swipes from right to left - does nothing by default
 * @param onSwipeRight Optional function to run when user swipes from left to right - does nothing by default
 * @param dragGestureMode A function with a [ZoomableState] scope that returns a [DragGestureMode] value that signals which drag gesture should currently be active. By default panning is enabled when zoomed, else swipe gestures are enabled.
 * @param doubleTapBehaviour A [DoubleTapBehaviour] providing a [DoubleTapBehaviour.onDoubleTap]. As [DoubleTapBehaviour] is a [functional Interface](https://kotlinlang.org/docs/fun-interfaces.html) you can just provide a lambda as a `onDoubleTap`
 */
@Composable
public fun ZoomableImage(
    coroutineScope: CoroutineScope,
    zoomableState: ZoomableState,
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    dragGestureMode: ZoomableState.() -> DragGestureMode = DragGestureMode.default,
    onTap: ((Offset) -> Unit)? = null,
    doubleTapBehaviour: DoubleTapBehaviour? = zoomableState.DefaultDoubleTapBehaviour(coroutineScope = coroutineScope)
) {
    Zoomable(
        coroutineScope = coroutineScope,
        zoomableState = zoomableState,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight,
        dragGestureMode = dragGestureMode,
        onTap = onTap,
        doubleTapBehaviour = doubleTapBehaviour
    ) {
        Image(bitmap = bitmap, contentDescription = contentDescription, modifier = modifier)
    }
}

/**
 * Creates a composable that shows an [ImageVector] and behaves like [Zoomable]
 *
 * @param coroutineScope used for smooth asynchronous zoom/pan/rotation animations
 * @param zoomableState Contains the current transform states - obtained via [rememberZoomableState]
 * @param imageVector The [ImageVector] to draw
 * @param modifier Modifier applied to the underlying [Image] Composable
 * @param contentDescription text for accessibility see [Image] for further info
 * @param onSwipeLeft Optional function to run when user swipes from right to left - does nothing by default
 * @param onSwipeRight Optional function to run when user swipes from left to right - does nothing by default
 * @param dragGestureMode A function with a [ZoomableState] scope that returns a [DragGestureMode] value that signals which drag gesture should currently be active. By default panning is enabled when zoomed, else swipe gestures are enabled.
 * @param doubleTapBehaviour A [DoubleTapBehaviour] providing a [DoubleTapBehaviour.onDoubleTap]. As [DoubleTapBehaviour] is a [functional Interface](https://kotlinlang.org/docs/fun-interfaces.html) you can just provide a lambda as a `onDoubleTap`
 */
@Composable
public fun ZoomableImage(
    coroutineScope: CoroutineScope,
    zoomableState: ZoomableState,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    dragGestureMode: ZoomableState.() -> DragGestureMode = DragGestureMode.default,
    onTap: ((Offset) -> Unit)? = null,
    doubleTapBehaviour: DoubleTapBehaviour? = zoomableState.DefaultDoubleTapBehaviour(coroutineScope = coroutineScope)
) {
    Zoomable(
        coroutineScope = coroutineScope,
        zoomableState = zoomableState,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight,
        dragGestureMode = dragGestureMode,
        onTap = onTap,
        doubleTapBehaviour = doubleTapBehaviour,
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}

/**
 * Creates a composable that shows an [Painter] and behaves like [Zoomable]
 *
 * @param coroutineScope used for smooth asynchronous zoom/pan/rotation animations
 * @param zoomableState Contains the current transform states - obtained via [rememberZoomableState]
 * @param painter The [Painter] to draw
 * @param modifier Modifier applied to the underlying [Image] Composable
 * @param contentDescription text for accessibility see [Image] for further info
 * @param onSwipeLeft Optional function to run when user swipes from right to left - does nothing by default
 * @param onSwipeRight Optional function to run when user swipes from left to right - does nothing by default
 * @param dragGestureMode A function with a [ZoomableState] scope that returns a [DragGestureMode] value that signals which drag gesture should currently be active. By default panning is enabled when zoomed, else swipe gestures are enabled.
 * @param doubleTapBehaviour A [DoubleTapBehaviour] providing a [DoubleTapBehaviour.onDoubleTap]. As [DoubleTapBehaviour] is a [functional Interface](https://kotlinlang.org/docs/fun-interfaces.html) you can just provide a lambda as a `onDoubleTap`
 */
@Composable
public fun ZoomableImage(
    coroutineScope: CoroutineScope,
    zoomableState: ZoomableState,
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    dragGestureMode: ZoomableState.() -> DragGestureMode = DragGestureMode.default,
    onTap: ((Offset) -> Unit)? = null,
    doubleTapBehaviour: DoubleTapBehaviour? = zoomableState.DefaultDoubleTapBehaviour(coroutineScope = coroutineScope)
) {
    Zoomable(
        coroutineScope = coroutineScope,
        zoomableState = zoomableState,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight,
        dragGestureMode = dragGestureMode,
        onTap = onTap,
        doubleTapBehaviour = doubleTapBehaviour
    ) {
        Image(painter = painter, contentDescription = contentDescription, modifier = modifier)
    }
}

/**
 * A simpler version of [ZoomableImage] which supplies it's own [CoroutineScope] and [ZoomableState]. It also doesn't allow you to change double tap behaviour
 *
 * @param bitmap The [ImageBitmap] to draw
 * @param modifier Modifier applied to the underlying [Image] Composable
 * @param contentDescription text for accessibility see [Image] for further info
 * @param onSwipeLeft Optional function to run when user swipes from right to left - does nothing by default
 * @param onSwipeRight Optional function to run when user swipes from left to right - does nothing by default
 */
@Composable
public fun EasyZoomableImage(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val zoomableState = rememberZoomableState()
    Zoomable(
        coroutineScope = coroutineScope,
        zoomableState = zoomableState,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight
    ) {
        Image(bitmap = bitmap, contentDescription = contentDescription, modifier = modifier)
    }
}

/**
 * A simpler version of [ZoomableImage] which supplies it's own [CoroutineScope] and [ZoomableState]. It also doesn't allow you to change double tap behaviour
 *
 * @param imageVector The [ImageVector] to draw
 * @param modifier Modifier applied to the underlying [Image] Composable
 * @param contentDescription text for accessibility see [Image] for further info
 * @param onSwipeLeft Optional function to run when user swipes from right to left - does nothing by default
 * @param onSwipeRight Optional function to run when user swipes from left to right - does nothing by default
 */
@Composable
public fun EasyZoomableImage(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val zoomableState = rememberZoomableState()

    Zoomable(
        coroutineScope = coroutineScope,
        zoomableState = zoomableState,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}

/**
 * A simpler version of [ZoomableImage] which supplies it's own [CoroutineScope] and [ZoomableState]. It also doesn't allow you to change double tap behaviour
 *
 * @param painter The [Painter] to draw
 * @param modifier Modifier applied to the underlying [Image] Composable
 * @param contentDescription text for accessibility see [Image] for further info
 * @param onSwipeLeft Optional function to run when user swipes from right to left - does nothing by default
 * @param onSwipeRight Optional function to run when user swipes from left to right - does nothing by default
 */
@Composable
public fun EasyZoomableImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val zoomableState = rememberZoomableState()
    Zoomable(
        coroutineScope = coroutineScope,
        zoomableState = zoomableState,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight
    ) {
        Image(painter = painter, contentDescription = contentDescription, modifier = modifier)
    }
}

