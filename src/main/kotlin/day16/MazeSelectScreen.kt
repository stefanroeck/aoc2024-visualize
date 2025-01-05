package day16

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

@Composable
fun MazeSelectScreen(
    eventHandler: EventHandler
) {
    val selectedMaze = remember { mutableStateOf(buttonOptions[0]) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                24.dp,
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MazeSelectorButton(selectedOption = selectedMaze.value) { new ->
                selectedMaze.value = new
            }

            ExtendedFloatingActionButton(
                onClick = {
                    eventHandler(AppEvent.OnSelectMaze(selectedMaze.value.resourceLocationOnClasspath))
                },
                icon = { Icon(Icons.Outlined.NotStarted, "Select maze") },
                text = { Text("Select maze") }
            )
        }
    }

}