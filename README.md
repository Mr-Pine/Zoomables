# Zoomables

[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/de.mr-pine.utils/zoomables/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/de.mr-pine.utils/zoomables)

A Jetpack Compose Library provides Composables that handle nice and smooth zooming behaviour for you

If you have any issues or ideas how to improve any of these libraries feel free to open an [issue](https://github.com/Mr-Pine/AndroidUtilityLibraries/issues/new/choose)

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

## How to use this library

Import via gradle using this version number: [![MavenCentral](https://maven-badges.herokuapp.com/maven-central/de.mr-pine.utils/zoomables/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/de.mr-pine.utils/zoomables)

`implementation "de.mr-pine.utils:zoomables:{Version number}"`
