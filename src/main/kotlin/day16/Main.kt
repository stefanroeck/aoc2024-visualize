package day16

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import day16.composables.MazeApplication

fun main() = application {

    val windowState = rememberWindowState(
        width = 940.dp,
        height = 1200.dp,
    )

    Window(onCloseRequest = ::exitApplication, state = windowState, title = "Maze Explorer") {
        MazeApplication(
            windowSize = windowState.size,
        )
    }
}
