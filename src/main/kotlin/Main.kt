import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import util.FileUtil
import util.InputUtils
import util.MapOfThings.Point
import util.Maze
import util.MazeElement
import util.MazeEvent
import util.MazeEventSink

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun App(maze: Maze, windowSize: DpSize): MazeEventSink {
    val eventSink: MazeEventSink = object : MazeEventSink {
        override fun onEvent(event: MazeEvent) {
            TODO("Not yet implemented")
        }
    }
    val boxSpacing = 1.dp
    val mazeColumns = maze.map.width
    val mazeRows = maze.map.height

    println("rendering a $mazeColumns x $mazeRows maze on window $windowSize")
    val boxSize = windowSize.width / mazeColumns - (boxSpacing + boxSpacing)
    val emptyColor = Color.LightGray
    val visitedColor = Color.Red
    val wallColor = Color.DarkGray
    val startEndColor = Color(0xFFFFB500)

    MaterialTheme {
        FlowRow(
            maxItemsInEachRow = mazeColumns,
            horizontalArrangement = Arrangement.spacedBy(boxSpacing),
            verticalArrangement = Arrangement.spacedBy(boxSpacing)
        ) {
            (0..<mazeColumns).forEach { col ->
                (0..<mazeRows).forEach { row ->
                    val point = Point(col, row)
                    val mazeElement = maze.map.thingAt(point)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                when (mazeElement) {
                                    MazeElement.Wall -> wallColor
                                    MazeElement.Start -> startEndColor
                                    MazeElement.End -> startEndColor
                                    else -> emptyColor
                                }
                            )
                            .height(boxSize)
                            .width(boxSize)
                    )
                }
            }
        }
    }

    return eventSink
}

fun main() = application {
    val maze = Maze(InputUtils.parseLines(FileUtil.readFile("/maze.txt")))
    val windowState = rememberWindowState(
        width = 940.dp,
        height = 1200.dp,
    )

    Window(onCloseRequest = ::exitApplication, state = windowState, title = "Maze Explorer") {
        val mazeEventSink = App(maze = maze, windowSize = windowState.size)
        maze.eventSinks.add(mazeEventSink)
    }
}
