package day16.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.RunCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import day16.AppEvent
import day16.EventHandler
import util.Maze
import util.MazeEvent
import util.MazeEventSink
import kotlin.math.min

private enum class State {
    None, Running, Finished
}

@Composable
fun ApplicationTopBarComposable(
    maze: Maze?,
    eventHandler: EventHandler
) {
    var currentCosts by remember { mutableStateOf(0L) }
    var foundSolutions by remember { mutableStateOf(0L) }
    var cheapestSolution by remember { mutableStateOf<Long?>(null) }
    var state by remember { mutableStateOf(State.None) }

    val eventSink = remember {
        object : MazeEventSink {
            override fun onEvent(event: MazeEvent) {
                when (event) {
                    is MazeEvent.FoundSolution -> {
                        foundSolutions += 1L
                        cheapestSolution =
                            if (cheapestSolution != null) min(cheapestSolution!!, event.costs) else event.costs
                    }
                    
                    is MazeEvent.Start -> {
                        state = State.Running
                    }

                    is MazeEvent.Movement -> {
                        currentCosts = event.costs
                    }

                    is MazeEvent.Finish -> {
                        state = State.Finished
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

    val text = when (state) {
        State.None -> "Hit 'Start' to explore the maze"
        State.Running -> "Found solutions: $foundSolutions, Cheapest solution: ${cheapestSolution ?: "n/a"}, Current costs: $currentCosts"
        State.Finished -> "Finished. Found solutions: $foundSolutions, Cheapest solution: ${cheapestSolution ?: "n/a"}"
    }

    TopAppBar(
        title = {
            if (state == State.Running) {
                Icon(Icons.Outlined.RunCircle, "Pending")
            } else if (state == State.Finished) {
                Icon(Icons.Outlined.Done, "Done")
            }
            Text(text, fontSize = 1.em, modifier = Modifier.padding(start = 4.dp))
        }, actions = {
            maze?.let {
                if (state == State.None) {
                    Button(
                        onClick = { eventHandler(AppEvent.OnStart) },
                    ) {
                        Text("Start")
                    }
                }
                if (state == State.Running) {
                    Button(
                        onClick = {
                            eventHandler(AppEvent.OnStop)
                            state = State.None
                        },
                    ) {
                        Text("Stop")
                    }
                }
                if (state in setOf(State.None, State.Finished)) {
                    Button(
                        onClick = {
                            eventHandler(AppEvent.OnStop)
                            state = State.None
                        },
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    )
}