package day16.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import day16.composables.SemanticPropertyKeys.MazeTile.MazeTileState
import util.MazeElement

private val emptyColor = Color(0xFFEEEEEE)
private val currentPositionColor = Color.Red
private val visitedColor = Color(0xFFAAAAAA)//Color(0xFFFFB500)
private val wallColor = Color.DarkGray
private val startEndColor = Color(0xFFf57c00)
private val cheapestSolutionColor = Color(0xFF81c784)
private val otherSolutionColor = Color(0xFF4fc3f7)

data class PointStats(
    val currentPosition: Boolean,
    val visited: Boolean,
    val cheapestSolution: Boolean,
    val otherSolution: Boolean,
)

@Composable
fun MazeTile(value: MazeElement?, stats: PointStats, boxSize: Dp, modifier: Modifier) {
    val backgroundColor = when (value) {
        MazeElement.Wall -> wallColor
        MazeElement.Start -> startEndColor
        MazeElement.End -> startEndColor
        else -> {
            if (stats.currentPosition) {
                currentPositionColor
            } else if (stats.cheapestSolution) {
                cheapestSolutionColor
            } else if (stats.otherSolution) {
                otherSolutionColor
            } else if (stats.visited) {
                visitedColor
            } else {
                emptyColor
            }
        }
    }
    val semanticsPropertyStates = mutableListOf<MazeTileState>()
    when (value) {
        MazeElement.Wall -> semanticsPropertyStates.add(MazeTileState.Wall)
        MazeElement.Start -> semanticsPropertyStates.add(MazeTileState.Start)
        MazeElement.End -> semanticsPropertyStates.add(MazeTileState.End)
        else -> {
            if (stats.currentPosition) {
                semanticsPropertyStates.add(MazeTileState.CurrentPosition)
            }
            if (stats.cheapestSolution) {
                semanticsPropertyStates.add(MazeTileState.ShortestSolution)
            }
            if (stats.otherSolution) {
                semanticsPropertyStates.add(MazeTileState.OtherSolutions)
            }
            if (stats.visited) {
                semanticsPropertyStates.add(MazeTileState.Visited)
            }
        }
    }
    Box(
        modifier = modifier.then(
            Modifier
                .background(backgroundColor)
                .testTag("mazeTile")
                .semantics { set(SemanticPropertyKeys.MazeTile.State, semanticsPropertyStates) }
                .height(boxSize)
                .width(boxSize)
        )
    )

}