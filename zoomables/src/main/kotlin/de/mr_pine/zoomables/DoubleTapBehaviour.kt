package de.mr_pine.zoomables

import androidx.compose.ui.geometry.Offset

public fun interface DoubleTapBehaviour {
    public fun onDoubleTap(offset: Offset)
}