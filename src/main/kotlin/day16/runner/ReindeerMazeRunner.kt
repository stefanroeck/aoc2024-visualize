package day16.runner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import day16.ReindeerMaze
import day16.ReindeerMazeFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import util.Maze

data class MazeLoaderSpec(val mazeResource: String)
data class MazeRunnerSpec(val showMovements: Boolean, val visualizationDelay: Long)

interface ReindeerMazeLifeCycle {
    fun load(spec: MazeLoaderSpec): Maze
    fun run(spec: MazeRunnerSpec)
    fun stop()
}

private class ReindeerMazeLifeCycleImpl : ReindeerMazeLifeCycle {
    private var reindeerMaze: ReindeerMaze? = null
    private var runningMazeJob: Job? = null

    override fun load(spec: MazeLoaderSpec): Maze {
        reindeerMaze = ReindeerMazeFactory.createReindeerMaze(spec.mazeResource)
        return reindeerMaze!!.maze
    }

    override fun run(spec: MazeRunnerSpec) {
        val maze = reindeerMaze?.maze ?: error("Call load() first")
        ReindeerMazeFactory.prepareForRun(spec.showMovements, spec.visualizationDelay, maze.events)
        if (runningMazeJob == null) {
            runningMazeJob = CoroutineScope(context = Dispatchers.Default).launch {
                reindeerMaze?.shortestPathCost()
            }
        }
    }

    override fun stop() {
        println("Cancelling $runningMazeJob")
        CoroutineScope(context = Dispatchers.Default).launch {
            reindeerMaze?.maze?.events?.doCancel = true
            runningMazeJob?.cancelAndJoin()
            runningMazeJob = null
            reindeerMaze = null
        }
    }

}

@Composable
fun rememberMazeLifecycle(): MutableState<ReindeerMazeLifeCycle> {
    return remember { mutableStateOf(ReindeerMazeLifeCycleImpl()) }
}