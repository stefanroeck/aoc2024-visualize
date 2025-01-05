package day16.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import util.MapOfThings.Point
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
    var solutionsWithCosts by remember { mutableStateOf(emptyList<Pair<Long, List<Point>>>()) }

    var cheapestSolution by remember { mutableStateOf(emptySet<Point>()) }
    var otherSolution by remember { mutableStateOf(emptySet<Point>()) }

    val eventSink = remember {
        object : MazeEventSink {
            override fun onEvent(event: MazeEvent) {
                if (event is MazeEvent.Movement) {
                    currentPosition = event.position
                    if (currentPosition !in visitedPoints) {
                        visitedPoints = visitedPoints + currentPosition
                    }
                } else if (event is MazeEvent.FoundSolution) {
                    solutionsWithCosts = solutionsWithCosts + (event.costs to event.path)
                    val lowestCosts = solutionsWithCosts.minBy { it.first }.first
                    cheapestSolution = solutionsWithCosts
                        .filter { it.first == lowestCosts }.flatMap { it.second }.toSet()
                    otherSolution = solutionsWithCosts
                        .filterNot { it.first == lowestCosts }.flatMap { it.second }.toSet()
                }
            }
        }
    }

    DisposableEffect(true) {
        events.register(eventSink)
        onDispose {
            events.unregister(eventSink)
        }
    }

    val boxSpacing = 1.dp
    val mazeColumns = map.width
    val mazeRows = map.height

    val boxSize = windowSize.width / mazeColumns - (boxSpacing + boxSpacing)

    FlowRow(
        maxItemsInEachRow = mazeColumns,
        horizontalArrangement = Arrangement.spacedBy(boxSpacing),
        verticalArrangement = Arrangement.spacedBy(boxSpacing)
    ) {
        (0..<mazeColumns).forEach { col ->
            (0..<mazeRows).forEach { row ->
                val point = Point(col, row)
                key(point) {
                    MazeTile(
                        map.thingAt(point),
                        PointStats(
                            currentPosition = point == currentPosition,
                            visited = point in visitedPoints,
                            cheapestSolution = point in cheapestSolution,
                            otherSolution = point in otherSolution
                        ),
                        boxSize,
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }
}