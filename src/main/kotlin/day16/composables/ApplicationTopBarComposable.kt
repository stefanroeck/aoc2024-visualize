package day16.composables

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import day16.AppEvent
import day16.EventHandler
import util.Maze

@Composable
fun ApplicationTopBarComposable(
    maze: Maze?,
    eventHandler: EventHandler
) {
    TopAppBar(
        title = {
            Text("Select maze to solve")
        }, actions = {
            maze?.let {
                Button(
                    onClick = { eventHandler(AppEvent.OnStart) },
                ) {
                    Text("Start")
                }
                Button(
                    onClick = { eventHandler(AppEvent.OnStop) },
                ) {
                    Text("Stop")
                }
            }
        }
    )
}