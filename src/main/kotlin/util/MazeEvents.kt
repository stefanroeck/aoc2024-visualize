package util

import util.MapOfThings.Point

class MazeEvents {
    private val eventSinks = mutableListOf<MazeEventSink>()
    var eventInvoker: MazeEventInvoker = DefaultMazeEventInvoker()
    var doCancel: Boolean = false

    fun register(eventSink: MazeEventSink) {
        eventSinks.add(eventSink)
    }

    fun unregister(eventSink: MazeEventSink) {
        eventSinks.remove(eventSink)
    }

    fun fire(event: MazeEvent) {
        eventInvoker.fire(event, eventSinks)
    }
}

sealed interface MazeEvent {
    data object Start : MazeEvent
    data class Movement(val position: Point, val costs: Long) : MazeEvent
    data class FoundSolution(val path: List<Point>, val costs: Long) : MazeEvent
    data object AbandonPath : MazeEvent
    data object Finish : MazeEvent
}

interface MazeEventSink {
    fun onEvent(event: MazeEvent)
}

interface MazeEventInvoker {
    fun fire(event: MazeEvent, eventSinks: List<MazeEventSink>)
}

class DefaultMazeEventInvoker(private val suppressedEvents: List<Class<out MazeEvent>> = emptyList()) :
    MazeEventInvoker {
    override fun fire(event: MazeEvent, eventSinks: List<MazeEventSink>) {
        if (suppressedEvents.none { it == event.javaClass }) {
            eventSinks.forEach { it.onEvent(event) }
        }
    }
}