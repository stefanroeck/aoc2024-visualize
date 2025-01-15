package day16.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotStarted
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import day16.SolutionStrategy

@Composable
fun MazeSelectScreen(
    mazeRenderingOptions: MazeRenderingOptions,
    onChangeMazeRenderingOptions: (MazeRenderingOptions) -> Unit,
    onStart: () -> Unit,
) {
    val selectedMaze = buttonOptions.first { it.resourceLocationOnClasspath == mazeRenderingOptions.mazeResource }
    val selectedSpeedOption = speedButtonOptions.first { it.delay == mazeRenderingOptions.visualizationDelay }
    val solutionStrategyOption = mazeRenderingOptions.solutionStrategy

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MazeSelectorButton(selectedOption = selectedMaze) { new ->
                onChangeMazeRenderingOptions(mazeRenderingOptions.copy(mazeResource = new.resourceLocationOnClasspath))
            }

            SpeedSelectorButton(selectedOption = selectedSpeedOption) { new ->
                onChangeMazeRenderingOptions(mazeRenderingOptions.copy(visualizationDelay = new.delay))
            }

            SolutionStrategySelectorButton(selectedOption = solutionStrategyOption) { new ->
                onChangeMazeRenderingOptions(mazeRenderingOptions.copy(solutionStrategy = new))
            }

            ShowMovementSwitch(selectedOption = mazeRenderingOptions.showMovements) { new ->
                onChangeMazeRenderingOptions(mazeRenderingOptions.copy(showMovements = new))
            }

            ExtendedFloatingActionButton(
                onClick = { onStart() },
                icon = { Icon(Icons.Outlined.NotStarted, "Start") },
                text = { Text("Start") }
            )
        }
    }

}

data class MazeRenderingOptions(
    val mazeResource: String,
    val showMovements: Boolean,
    val visualizationDelay: Long,
    val solutionStrategy: SolutionStrategy
)

val defaultMazeRenderingOptions =
    MazeRenderingOptions(
        buttonOptions[0].resourceLocationOnClasspath,
        true,
        speedButtonOptions[0].delay,
        SolutionStrategy.LowestCost
    )