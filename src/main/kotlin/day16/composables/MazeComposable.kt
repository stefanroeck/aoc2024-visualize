package day16.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import util.MapOfThings.Point
import util.MazeElement
import util.MazeEvent
import util.MazeEventSink
import util.MazeEvents
import util.MazeMap

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MazeComposable(
    map: MazeMap,
    startPosition: Point,
    windowSize: DpSize,
    events: MazeEvents,
) {
    var currentPosition by remember { mutableStateOf(startPosition) }
    var visitedPoints by remember { mutableStateOf(emptySet<Point>()) }

    val eventSink = remember {
        object : MazeEventSink {
            override fun onEvent(event: MazeEvent) {
                if (event is MazeEvent.Movement) {
                    currentPosition = event.position
                    visitedPoints = visitedPoints + currentPosition
                }
            }
        }
    }

    LaunchedEffect("maze") {
        println("register event")
        events.register(eventSink)
    }

    DisposableEffect("maze") {
        println("unregister event")
        onDispose {
            events.unregister(eventSink)
        }
    }

    val boxSpacing = 1.dp
    val mazeColumns = map.width
    val mazeRows = map.height

    val boxSize = windowSize.width / mazeColumns - (boxSpacing + boxSpacing)
    val emptyColor = Color.LightGray
    val currentPositionColor = Color.Red
    val visitedColor = Color(0xFFFFB500)
    val wallColor = Color.DarkGray
    val startEndColor = Color.Magenta

    FlowRow(
        maxItemsInEachRow = mazeColumns,
        horizontalArrangement = Arrangement.spacedBy(boxSpacing),
        verticalArrangement = Arrangement.spacedBy(boxSpacing)
    ) {
        (0..<mazeColumns).forEach { col ->
            (0..<mazeRows).forEach { row ->
                val point = Point(col, row)
                val mazeElement = map.thingAt(point)
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
                key(point) {
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