package day16

import day16.runner.AnimatingEventFilter
import day16.runner.ScopedEventFilter
import day16.runner.StopEventPropagationOnCancelEventFilter
import day16.runner.SuppressingEventFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import util.FileUtil
import util.FilteringMazeEventBroker
import util.InputUtils
import util.MazeEvent
import util.MazeEvents
import java.time.Duration

object ReindeerMazeFactory {

    fun createReindeerMaze(mazeResource: String): ReindeerMaze {
        return runBlocking {
            loadMaze(mazeResource)
        }
    }

    fun prepareForRun(showMovements: Boolean, visualizationDelay: Long, mazeEvents: MazeEvents) {
        val suppressedEvents = if (!showMovements) {
            listOf(MazeEvent.Movement::class.java, MazeEvent.AbandonPath::class.java)
        } else {
            emptyList()
        }

        val broker = FilteringMazeEventBroker(
            listOf(
                StopEventPropagationOnCancelEventFilter(isCanceled = { mazeEvents.doCancel }),
                SuppressingEventFilter(suppressedEvents),
                AnimatingEventFilter(animationDelay = Duration.ofMillis(visualizationDelay)),
                ScopedEventFilter(scope = CoroutineScope(context = Dispatchers.Main))
            )
        )

        mazeEvents.eventInvoker = broker
    }

    private suspend fun loadMaze(filePath: String): ReindeerMaze {
        val lines = InputUtils.parseLines(FileUtil.readComposeResource(filePath))
        return ReindeerMaze(lines, DirectionStrategies.LowestCost)
    }

}