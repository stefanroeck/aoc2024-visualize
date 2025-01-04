package day16

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import util.FileUtil
import util.InputUtils
import util.MapOfThings.Point
import util.Maze
import util.MazeElement
import util.MazeEvent
import util.MazeEventSink

enum class AppEvent {
    OnStart, OnStop
}

typealias EventHandler = (event: AppEvent) -> Unit

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun App(
    maze: Maze,
    windowSize: DpSize,
    currentPosition: Point,
    visitedPoints: Set<Point>,
    eventHandler: EventHandler
) {
    val boxSpacing = 1.dp
    val mazeColumns = maze.map.width
    val mazeRows = maze.map.height

    val boxSize = windowSize.width / mazeColumns - (boxSpacing + boxSpacing)
    val emptyColor = Color.LightGray
    val currentPositionColor = Color.Red
    val visitedColor = Color(0xFFFFB500)
    val wallColor = Color.DarkGray
    val startEndColor = Color.Magenta

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.height(40.dp),
                    title = {
                        Text("Select maze to solve")
                    }, actions = {
                        Button(onClick = { eventHandler(AppEvent.OnStart) }) {
                            Text("Start")
                        }
                        Button(onClick = { eventHandler(AppEvent.OnStop) }) {
                            Text("Stop")
                        }
                    }
                )
            },
        ) { _ ->
            FlowRow(
                maxItemsInEachRow = mazeColumns,
                horizontalArrangement = Arrangement.spacedBy(boxSpacing),
                verticalArrangement = Arrangement.spacedBy(boxSpacing)
            ) {
                (0..<mazeColumns).forEach { col ->
                    (0..<mazeRows).forEach { row ->
                        val point = Point(col, row)
                        val mazeElement = maze.map.thingAt(point)
                        val backgroundColor = when (point) {
                            currentPosition -> currentPositionColor
                            in visitedPoints -> visitedColor
                            else -> when (mazeElement) {
                                MazeElement.Wall -> wallColor
                                MazeElement.Start -> startEndColor
                                MazeElement.End -> startEndColor
                                else -> emptyColor
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(backgroundColor)
                                .height(boxSize)
                                .width(boxSize)
                        )
                    }
                }
            }
        }
    }
}

fun main() = application {
    val reindeerMaze = ReindeerMaze(InputUtils.parseLines(FileUtil.readFile("/maze.txt")))
    val maze = reindeerMaze.maze
    var currentPosition by remember { mutableStateOf(maze.startPosition) }
    var visitedPoints by remember { mutableStateOf(emptySet<Point>()) }

    val mainScope = CoroutineScope(context = Dispatchers.Main)
    var runningMazeJob: Job? = null

    val eventSink: MazeEventSink = object : MazeEventSink {
        override fun onEvent(event: MazeEvent) {
            if (runningMazeJob?.isCancelled == true) {
                throw IllegalStateException("Cancel maze traversal")
            }

            Thread.sleep(50)
            mainScope.launch {
                when (event) {
                    MazeEvent.Abort -> {}
                    MazeEvent.FoundSolution -> {}
                    is MazeEvent.Movement -> {
                        currentPosition = event.position
                        visitedPoints = visitedPoints + currentPosition
                    }
                }
            }
        }
    }
    maze.eventSinks.add(eventSink)

    val windowState = rememberWindowState(
        width = 940.dp,
        height = 1200.dp,
    )

    val eventHandler = object : EventHandler {
        override fun invoke(event: AppEvent) {
            when (event) {
                AppEvent.OnStart -> {
                    if (runningMazeJob == null) {
                        runningMazeJob = CoroutineScope(context = Dispatchers.Default).launch {
                            currentPosition = maze.startPosition
                            visitedPoints = emptySet()
                            reindeerMaze.shortestPathCost()
                        }
                    }
                }

                AppEvent.OnStop -> {
                    println("Try to cancel $runningMazeJob")
                    CoroutineScope(context = Dispatchers.Default).launch {
                        runningMazeJob?.cancelAndJoin()
                        runningMazeJob = null
                    }
                }
            }
        }

    }

    Window(onCloseRequest = ::exitApplication, state = windowState, title = "Maze Explorer") {
        App(
            maze = maze,
            windowSize = windowState.size,
            currentPosition,
            visitedPoints,
            eventHandler
        )
    }
}
