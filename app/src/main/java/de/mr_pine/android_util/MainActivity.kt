package de.mr_pine.android_util

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import de.mr_pine.android_util.ui.theme.AndroidUtilityLibrariesTheme
import de.mr_pine.zoomable.EasyZoomableImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidUtilityLibrariesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {Text(text = "Zoom comparison")}
                            )
                        }
                    ) {
                        Column {
                            var currentTab by remember { mutableStateOf(0) }
                            TabRow(selectedTabIndex = currentTab) {
                                Tab(
                                    text = { Text(text = "Zoomable implementation") },
                                    selected = currentTab == 0,
                                    onClick = { currentTab = 0 }
                                )
                                Tab(
                                    text = { Text(text = "Default implementation") },
                                    selected = currentTab == 1,
                                    onClick = { currentTab = 1 }
                                )
                            }
                            if (currentTab == 0) {
                                val context = LocalContext.current
                                EasyZoomableImage(
                                    painter = painterResource(id = R.drawable.phoenix),
                                    modifier = Modifier.fillMaxSize(),
                                    onSwipeRight = {
                                        Toast.makeText(
                                            context, "Swipe right", Toast.LENGTH_LONG
                                        ).show()
                                    }, onSwipeLeft = {
                                        Toast.makeText(
                                            context, "Swipe left", Toast.LENGTH_LONG
                                        ).show()
                                    })
                            } else if (currentTab == 1) {

                                var scale by remember { mutableStateOf(1f) }
                                var rotation by remember { mutableStateOf(0f) }
                                var offset by remember { mutableStateOf(Offset.Zero) }
                                val coroutineScope = rememberCoroutineScope()

                                val state =
                                    rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                                        scale *= zoomChange
                                        rotation += rotationChange
                                        offset += offsetChange
                                    }

                                Box(
                                    Modifier
                                        // apply pan offset state as a layout transformation before other modifiers
                                        .offset {
                                            IntOffset(
                                                offset.x.roundToInt(),
                                                offset.y.roundToInt()
                                            )
                                        }
                                        // add transformable to listen to multitouch transformation events after offset
                                        .transformable(state = state)
                                        // optional for example: add double click to zoom
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onDoubleTap = {
                                                    coroutineScope.launch { state.animateZoomBy(if(scale != 1f) 1/scale else 4f) }
                                                }
                                            )
                                        }
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.phoenix),
                                        contentDescription = "image",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer(
                                                scaleX = scale,
                                                scaleY = scale,
                                                rotationZ = rotation
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidUtilityLibrariesTheme {
        Greeting("Android")
    }
}