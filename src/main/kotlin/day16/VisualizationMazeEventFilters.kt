package day16

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import util.MazeEvent
import util.MazeEventFilter
import util.MazeEventSink
import java.time.Duration

class ScopedEventFilter(
    private val scope: CoroutineScope,
) : MazeEventFilter {

    override fun filter(event: MazeEvent, eventSinks: List<MazeEventSink>, filterChain: MazeEventFilter.FilterChain) {
        scope.launch {
            filterChain.doContinue(event, eventSinks)
        }
    }
}

class StopEventPropagationOnCancelEventFilter(
    private val isCanceled: () -> Boolean,
) : MazeEventFilter {

    override fun filter(event: MazeEvent, eventSinks: List<MazeEventSink>, filterChain: MazeEventFilter.FilterChain) {
        if (!isCanceled()) {
            filterChain.doContinue(event, eventSinks)
        }
    }
}

class AnimatingEventFilter(
    private val animationDelay: Duration,
) : MazeEventFilter {

    override fun filter(event: MazeEvent, eventSinks: List<MazeEventSink>, filterChain: MazeEventFilter.FilterChain) {
        if (!animationDelay.isZero) {
            Thread.sleep(animationDelay)
        }
        filterChain.doContinue(event, eventSinks)
    }
}

class SuppressingEventFilter(private val suppressedEvents: List<Class<out MazeEvent>>) : MazeEventFilter {
    override fun filter(event: MazeEvent, eventSinks: List<MazeEventSink>, filterChain: MazeEventFilter.FilterChain) {
        if (suppressedEvents.none { it == event.javaClass }) {
            filterChain.doContinue(event, eventSinks)
        }
    }
}