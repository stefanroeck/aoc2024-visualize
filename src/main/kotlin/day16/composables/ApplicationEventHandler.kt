package day16.composables

sealed interface AppEvent {
    data class OnSelectMaze(val mazeResource: String, val options: VisualizationOptions) : AppEvent
    data object OnStart : AppEvent
    data object OnStop : AppEvent
}
typealias EventHandler = (event: AppEvent) -> Unit