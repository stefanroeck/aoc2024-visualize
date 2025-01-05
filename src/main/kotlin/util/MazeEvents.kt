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
    data class Movement(val position: Point, val costs: Long) : MazeEvent
    data class FoundSolution(val path: List<Point>, val costs: Long) : MazeEvent
    data object Abort : MazeEvent
}

interface MazeEventSink {
    fun onEvent(event: MazeEvent)
}

interface MazeEventInvoker {
    fun fire(event: MazeEvent, eventSinks: List<MazeEventSink>)
}

class DefaultMazeEventInvoker : MazeEventInvoker {
    override fun fire(event: MazeEvent, eventSinks: List<MazeEventSink>) {
        eventSinks.forEach { it.onEvent(event) }
    }
}