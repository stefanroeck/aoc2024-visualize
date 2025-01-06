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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import day16.AppEvent
import day16.AppEvent.OnSelectMaze.VisualizationOptions
import day16.EventHandler

@Composable
fun MazeSelectScreen(
    eventHandler: EventHandler
) {
    val selectedMaze = remember { mutableStateOf(buttonOptions[0]) }
    val selectedSpeedOption = remember { mutableStateOf(SpeedSelectorOption(speedButtonOptions[0], true)) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MazeSelectorButton(selectedOption = selectedMaze.value) { new ->
                selectedMaze.value = new
            }
            SpeedSelectorButton(selectedOption = selectedSpeedOption.value) { new ->
                selectedSpeedOption.value = new
            }

            ExtendedFloatingActionButton(
                onClick = {
                    eventHandler(
                        AppEvent.OnSelectMaze(
                            mazeResource = selectedMaze.value.resourceLocationOnClasspath,
                            options = VisualizationOptions(
                                showMovements = selectedSpeedOption.value.showMovement,
                                visualizationDelay = selectedSpeedOption.value.buttonOption.delay
                            ),
                        )
                    )
                },
                icon = { Icon(Icons.Outlined.NotStarted, "Start") },
                text = { Text("Start") }
            )
        }
    }

}