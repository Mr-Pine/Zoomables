# Jetpack Compose Zoomables

This library provides Composables that handle nice and smooth zooming behaviour for you

<details>
  <summary>Show comparison</summary>
  
  ### Comparison between this library and the way recommended by the Android documentation
  
  Notice that the rotation and zoom are centered at the touch point with this library but at the center of the image with the other option
  
  ![](Zoom_comparison.gif)
  
</details>

## Why use this library?

- It provides nicer zooming behaviour (see comparison)
- Provides callback functions to handle swiping left/right on the image when not zoomed in
- Reduces difficult to read code

## What does this library provide?

- General Composables for with zooming behaviour
- Special Composables for Images, reducing boilerplate code even further
- A ZoomableState
