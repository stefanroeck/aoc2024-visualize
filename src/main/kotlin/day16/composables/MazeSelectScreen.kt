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
import day16.MazeRenderingOptions

@Composable
fun MazeSelectScreen(
    mazeRenderingOptions: MazeRenderingOptions?,
    onChangeMazeRenderingOptions: (MazeRenderingOptions) -> Unit,
    onStart: () -> Unit,
) {
    val mazeOptionsOrDefault = mazeRenderingOptions ?: MazeRenderingOptions(
        buttonOptions[0].resourceLocationOnClasspath,
        true,
        speedButtonOptions[0].delay,
    )
    val selectedMaze = buttonOptions.first { it.resourceLocationOnClasspath == mazeOptionsOrDefault.mazeResource }
    val selectedSpeedOption = speedButtonOptions.first { it.delay == mazeOptionsOrDefault.visualizationDelay }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MazeSelectorButton(selectedOption = selectedMaze) { new ->
                onChangeMazeRenderingOptions(mazeOptionsOrDefault.copy(mazeResource = new.resourceLocationOnClasspath))
            }

            SpeedSelectorButton(selectedOption = selectedSpeedOption) { new ->
                onChangeMazeRenderingOptions(mazeOptionsOrDefault.copy(visualizationDelay = new.delay))
            }

            ShowMovementSwitch(selectedOption = mazeOptionsOrDefault.showMovements) { new ->
                onChangeMazeRenderingOptions(mazeOptionsOrDefault.copy(showMovements = new))
            }

            ExtendedFloatingActionButton(
                onClick = { onStart() },
                icon = { Icon(Icons.Outlined.NotStarted, "Start") },
                text = { Text("Start") }
            )
        }
    }

}