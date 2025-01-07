package day16.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import util.Maze


@Composable
fun MazeApplication(
    maze: Maze?,
    windowSize: DpSize,
    eventHandler: EventHandler
) {
    val mazeRenderingOptions = remember { mutableStateOf<MazeRenderingOptions?>(null) }

    MaterialTheme {
        Scaffold(
            topBar = { ApplicationTopBarComposable(maze, eventHandler) }
        ) { _ ->
            if (maze != null) {
                MazeComposable(maze.map, maze.startPosition, windowSize, maze.events)
            } else {
                MazeSelectScreen(
                    mazeRenderingOptions = mazeRenderingOptions.value,
                    onChangeMazeRenderingOptions = { new -> mazeRenderingOptions.value = new },
                    onStart = {
                        mazeRenderingOptions.value?.let {
                            eventHandler.invoke(
                                AppEvent.OnSelectMaze(
                                    it.mazeResource,
                                    VisualizationOptions(it.showMovements, it.visualizationDelay)
                                )
                            )
                        }
                    }
                )
            }
        }
    }

}

data class VisualizationOptions(val showMovements: Boolean, val visualizationDelay: Long)
