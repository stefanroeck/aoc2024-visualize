package day16

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import day16.composables.ApplicationTopBarComposable
import day16.composables.MazeComposable
import day16.composables.MazeSelectScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import util.FileUtil
import util.InputUtils
import util.MapOfThings.Point
import util.Maze
import util.MazeEvent
import util.MazeEventSink

sealed interface AppEvent {
    data class OnSelectMaze(val mazeResource: String) : AppEvent
    data object OnStart : AppEvent
    data object OnStop : AppEvent
}


typealias EventHandler = (event: AppEvent) -> Unit

@Composable
@Preview
fun App(
    maze: Maze?,
    windowSize: DpSize,
    currentPosition: Point,
    visitedPoints: Set<Point>,
    eventHandler: EventHandler
) {
    MaterialTheme {
        Scaffold(
            topBar = { ApplicationTopBarComposable(maze, eventHandler) }
        ) { _ ->
            val map = maze?.map
            if (map != null) {
                MazeComposable(map, windowSize, currentPosition, visitedPoints)
            } else {
                MazeSelectScreen(eventHandler)
            }
        }
    }
}

private fun loadMaze(filePath: String) = ReindeerMaze(InputUtils.parseLines(FileUtil.readFile(filePath)))

fun main() = application {
    var currentPosition by remember { mutableStateOf(Point(0, 0)) }
    var visitedPoints by remember { mutableStateOf(emptySet<Point>()) }
    var reindeerMaze: ReindeerMaze? by remember { mutableStateOf(null) }

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

    val windowState = rememberWindowState(
        width = 940.dp,
        height = 1200.dp,
    )

    val eventHandler = object : EventHandler {
        override fun invoke(event: AppEvent) {
            when (event) {
                is AppEvent.OnSelectMaze -> {
                    reindeerMaze = loadMaze(event.mazeResource)
                    currentPosition = reindeerMaze!!.maze.startPosition
                    reindeerMaze!!.maze.eventSinks.add(eventSink)
                    visitedPoints = emptySet()
                }

                is AppEvent.OnStart -> {
                    if (runningMazeJob == null) {
                        runningMazeJob = CoroutineScope(context = Dispatchers.Default).launch {
                            reindeerMaze?.let {
                                currentPosition = it.maze.startPosition
                                visitedPoints = emptySet()
                                it.shortestPathCost()
                            }
                        }
                    }
                }

                AppEvent.OnStop -> {
                    println("Try to cancel $runningMazeJob")
                    CoroutineScope(context = Dispatchers.Default).launch {
                        runningMazeJob?.cancelAndJoin()
                        runningMazeJob = null
                        reindeerMaze = null
                    }
                }

            }
        }

    }

    Window(onCloseRequest = ::exitApplication, state = windowState, title = "Maze Explorer") {
        App(
            maze = reindeerMaze?.maze,
            windowSize = windowState.size,
            currentPosition,
            visitedPoints,
            eventHandler
        )
    }
}
