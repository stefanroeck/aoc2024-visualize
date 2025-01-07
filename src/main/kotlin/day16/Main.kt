package day16

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import day16.composables.AppEvent
import day16.composables.EventHandler
import day16.composables.MazeApplication
import day16.runner.AnimatingEventFilter
import day16.runner.ScopedEventFilter
import day16.runner.StopEventPropagationOnCancelEventFilter
import day16.runner.SuppressingEventFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import util.FileUtil
import util.FilteringMazeEventBroker
import util.InputUtils
import util.MazeEvent
import java.time.Duration

private fun loadMaze(filePath: String) = ReindeerMaze(InputUtils.parseLines(FileUtil.readFile(filePath)))

fun main() = application {
    var reindeerMaze: ReindeerMaze? by remember { mutableStateOf(null) }

    val eventHandler = object : EventHandler {
        var runningMazeJob: Job? = null
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

    val windowState = rememberWindowState(
        width = 940.dp,
        height = 1200.dp,
    )

    Window(onCloseRequest = ::exitApplication, state = windowState, title = "Maze Explorer") {
        MazeApplication(
            maze = reindeerMaze?.maze,
            windowSize = windowState.size,
            eventHandler
        )
    }
}
