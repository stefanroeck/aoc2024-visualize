package day16.composables

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.em
import day16.AppEvent
import day16.EventHandler
import util.Maze
import util.MazeEvent
import util.MazeEventSink
import kotlin.math.min

@Composable
fun ApplicationTopBarComposable(
    maze: Maze?,
    eventHandler: EventHandler
) {
    var currentCosts by remember { mutableStateOf(0L) }
    var foundSolutions by remember { mutableStateOf(0L) }
    var cheapestSolution by remember { mutableStateOf<Long?>(null) }

    val eventSink = remember {
        object : MazeEventSink {
            override fun onEvent(event: MazeEvent) {
                when (event) {
                    is MazeEvent.FoundSolution -> {
                        foundSolutions += 1L
                        cheapestSolution =
                            if (cheapestSolution != null) min(cheapestSolution!!, event.costs) else event.costs
                    }

                    is MazeEvent.Movement -> {
                        currentCosts = event.costs
                    }

                    else -> {}
                }
            }
        }
    }

    DisposableEffect(maze) {
        maze?.events?.register(eventSink)
        onDispose {
            currentCosts = 0L
            foundSolutions = 0L
            cheapestSolution = null
            maze?.events?.unregister(eventSink)
        }
    }

    val text = if (maze == null) {
        "Hit 'Start' to explore the maze"
    } else {
        "Found solutions: $foundSolutions, Cheapest solution: ${cheapestSolution ?: "n/a"}, Current costs: $currentCosts"
    }

    TopAppBar(
        title = {
            Text(text, fontSize = 1.em)
        }, actions = {
            maze?.let {
                Button(
                    onClick = { eventHandler(AppEvent.OnStart) },
                ) {
                    Text("Start")
                }
                Button(
                    onClick = { eventHandler(AppEvent.OnStop) },
                ) {
                    Text("Stop")
                }
            }
        }
    )
}