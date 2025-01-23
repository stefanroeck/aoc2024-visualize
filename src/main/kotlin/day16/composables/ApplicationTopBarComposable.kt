package day16.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import util.MapOfThings.Direction
import util.Maze
import util.MazeEvent
import util.MazeEventSink
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.min

private enum class State {
    None, Running, Finished
}

private fun formatLong(value: Long?): String? {
    if (value == null) return null
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = ' ' // Use a space as the thousands separator
    }
    return DecimalFormat("#,###", symbols).format(value)
}


@Composable
fun ApplicationTopBarComposable(
    maze: Maze?,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    var currentCosts by remember { mutableStateOf(0L) }
    var totalSteps by remember { mutableStateOf(0L) }
    var foundSolutions by remember { mutableStateOf(0L) }
    var cheapestSolution by remember { mutableStateOf<Long?>(null) }
    var state by remember { mutableStateOf(State.None) }
    var direction by remember { mutableStateOf(Direction.Right) }

    val eventSink = remember {
        object : MazeEventSink {
            override fun onEvent(event: MazeEvent) {
                when (event) {
                    is MazeEvent.FoundSolution -> {
                        foundSolutions += 1L
                        cheapestSolution =
                            if (cheapestSolution != null) min(cheapestSolution!!, event.costs) else event.costs
                        totalSteps = event.steps
                    }

                    is MazeEvent.Start -> {
                        state = State.Running
                    }

                    is MazeEvent.Movement -> {
                        currentCosts = event.costs
                        totalSteps = event.steps
                        direction = event.direction
                    }

                    is MazeEvent.Finish -> {
                        state = State.Finished
                        totalSteps = event.steps
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
            totalSteps = 0L
            cheapestSolution = null
            maze?.events?.unregister(eventSink)
        }
    }

    val text = when (state) {
        State.None -> "Hit 'Start' to explore the maze"
        State.Running -> "Found solutions: $foundSolutions, Cheapest solution: ${formatLong(cheapestSolution) ?: "n/a"}, " +
                "Current costs: ${formatLong(currentCosts)}, Steps: ${formatLong(totalSteps)}"

        State.Finished -> "Finished. Found solutions: $foundSolutions, Cheapest solution: ${formatLong(cheapestSolution) ?: "n/a"}, " +
                "Steps: ${formatLong(totalSteps)}"
    }

    val directionIcon = when (direction) {
        Direction.Left -> Icons.Outlined.ArrowCircleLeft
        Direction.Right -> Icons.Outlined.ArrowCircleRight
        Direction.Up -> Icons.Outlined.ArrowCircleUp
        Direction.Down -> Icons.Outlined.ArrowCircleDown
        else -> {
            null
        }
    }

    TopAppBar(
        title = {
            if (state == State.Running && directionIcon != null) {
                Icon(directionIcon, "Pending")
            } else if (state == State.Finished) {
                Icon(Icons.Outlined.Done, "Done")
            }
            Text(text, fontSize = 1.em, modifier = Modifier.padding(start = 4.dp))
        }, actions = {
            maze?.let {
                if (state == State.None) {
                    Button(
                        onClick = onStart,
                    ) {
                        Text("Start")
                    }
                }
                if (state == State.Running) {
                    Button(
                        onClick = {
                            onStop()
                            state = State.None
                        },
                    ) {
                        Text("Stop")
                    }
                }
                if (state in setOf(State.None, State.Finished)) {
                    Button(
                        onClick = {
                            onStop()
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