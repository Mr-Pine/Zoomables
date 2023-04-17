package de.mr_pine.zoomables

public enum class DragGestureMode {
    DISABLED,
    PAN,
    SWIPE_GESTURES;

    public companion object {
        public val default: ZoomableState.() -> DragGestureMode =
            { if (transformed) PAN else SWIPE_GESTURES }
    }
}