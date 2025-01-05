package util

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
    data class Movement(val position: MapOfThings.Point) : MazeEvent
    data object FoundSolution : MazeEvent
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