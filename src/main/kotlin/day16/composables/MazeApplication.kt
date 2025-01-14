package day16.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import day16.runner.MazeLoaderSpec
import day16.runner.MazeRunnerSpec
import day16.runner.rememberMazeLifecycle
import util.Maze


@Composable
fun MazeApplication(
    windowSize: DpSize,
) {
    val lifeCycle by rememberMazeLifecycle()
    val mazeHolder = remember { mutableStateOf<Maze?>(null) }
    val mazeRenderingOptions = remember { mutableStateOf(defaultMazeRenderingOptions) }

    val maze = mazeHolder.value

    MaterialTheme {
        Scaffold(
            topBar = {
                ApplicationTopBarComposable(
                    maze,
                    onStart = {
                        lifeCycle.run(
                            MazeRunnerSpec(
                                mazeRenderingOptions.value.showMovements,
                                mazeRenderingOptions.value.visualizationDelay
                            )
                        )
                    },
                    onStop = {
                        lifeCycle.stop()
                        mazeHolder.value = null
                    })
            }
        ) { _ ->
            if (maze != null) {
                MazeComposable(maze.map, maze.startPosition, windowSize, maze.events)
            } else {
                MazeSelectScreen(
                    mazeRenderingOptions = mazeRenderingOptions.value,
                    onChangeMazeRenderingOptions = { new -> mazeRenderingOptions.value = new },
                    onStart = {
                        mazeRenderingOptions.value.run {
                            mazeHolder.value = lifeCycle.load(MazeLoaderSpec(mazeResource))
                        }
                    }
                )
            }
        }
    }

}

data class VisualizationOptions(val showMovements: Boolean, val visualizationDelay: Long)
