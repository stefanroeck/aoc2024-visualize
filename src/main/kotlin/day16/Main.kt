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
import util.FilteringMazeEventBroker
import util.InputUtils
import util.Maze
import util.MazeEvent
import java.time.Duration

sealed interface AppEvent {
    data class OnSelectMaze(val mazeResource: String, val options: VisualizationOptions) : AppEvent {
        data class VisualizationOptions(val showMovements: Boolean, val visualizationDelay: Long)
    }

    data object OnStart : AppEvent
    data object OnStop : AppEvent
}


typealias EventHandler = (event: AppEvent) -> Unit

@Composable
@Preview
fun App(
    maze: Maze?,
    windowSize: DpSize,
    eventHandler: EventHandler
) {
    MaterialTheme {
        Scaffold(
            topBar = { ApplicationTopBarComposable(maze, eventHandler) }
        ) { _ ->
            if (maze != null) {
                MazeComposable(maze.map, maze.startPosition, windowSize, maze.events)
            } else {
                MazeSelectScreen(eventHandler)
            }
        }
    }
}

private fun loadMaze(filePath: String) = ReindeerMaze(InputUtils.parseLines(FileUtil.readFile(filePath)))

fun main() = application {
    var reindeerMaze: ReindeerMaze? by remember { mutableStateOf(null) }

    var runningMazeJob: Job? = null

    val windowState = rememberWindowState(
        width = 940.dp,
        height = 1200.dp,
    )

    val eventHandler = object : EventHandler {
        override fun invoke(event: AppEvent) {
            when (event) {
                is AppEvent.OnSelectMaze -> {
                    reindeerMaze = loadMaze(event.mazeResource).also {
                        val suppressedEvents = if (!event.options.showMovements) {
                            listOf(MazeEvent.Movement::class.java, MazeEvent.AbandonPath::class.java)
                        } else {
                            emptyList()
                        }
                        
                        val broker = FilteringMazeEventBroker(
                            listOf(
                                StopEventPropagationOnCancelEventFilter(isCanceled = { it.maze.events.doCancel }),
                                SuppressingEventFilter(suppressedEvents),
                                AnimatingEventFilter(animationDelay = Duration.ofMillis(event.options.visualizationDelay)),
                                ScopedEventFilter(scope = CoroutineScope(context = Dispatchers.Main))
                            )
                        )

                        it.maze.events.eventInvoker = broker
                    }
                }

                is AppEvent.OnStart -> {
                    if (runningMazeJob == null) {
                        runningMazeJob = CoroutineScope(context = Dispatchers.Default).launch {
                            reindeerMaze?.shortestPathCost()
                        }
                    }
                }

                AppEvent.OnStop -> {
                    println("Cancelling $runningMazeJob")
                    CoroutineScope(context = Dispatchers.Default).launch {
                        reindeerMaze?.maze?.events?.doCancel = true
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
            eventHandler
        )
    }
}
