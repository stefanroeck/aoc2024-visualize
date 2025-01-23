package util

import util.MapOfThings.Direction
import util.MapOfThings.Point

class MazeEvents {
    private val eventSinks = mutableListOf<MazeEventSink>()
    var eventInvoker: MazeEventBroker = FilteringMazeEventBroker()
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
    data class Movement(val position: Point, val direction: Direction, val costs: Long, val steps: Long) : MazeEvent
    data class FoundSolution(val path: List<Point>, val costs: Long, val steps: Long) : MazeEvent
    data object AbandonPath : MazeEvent
    data class Finish(val steps: Long) : MazeEvent
}

interface MazeEventSink {
    fun onEvent(event: MazeEvent)
}

interface MazeEventBroker {
    fun fire(event: MazeEvent, eventSinks: List<MazeEventSink>)
}

interface MazeEventFilter {
    interface FilterChain {
        fun doContinue(event: MazeEvent, eventSinks: List<MazeEventSink>)
    }

    fun filter(event: MazeEvent, eventSinks: List<MazeEventSink>, filterChain: FilterChain)
}

class FilteringMazeEventBroker(private val filters: List<MazeEventFilter> = emptyList()) :
    MazeEventBroker {

    fun buildFilterChain(
        index: Int,
        filters: List<MazeEventFilter>,
        terminator: (MazeEvent, List<MazeEventSink>) -> Unit
    ): MazeEventFilter.FilterChain {
        val currentFilter = filters[index]
        val filterChain = object : MazeEventFilter.FilterChain {
            override fun doContinue(event: MazeEvent, eventSinks: List<MazeEventSink>) {
                val nextIndex = index + 1
                if (nextIndex in filters.indices) {
                    currentFilter.filter(event, eventSinks, buildFilterChain(nextIndex, filters, terminator))
                } else {
                    terminator(event, eventSinks)
                }
            }
        }
        return filterChain
    }

    override fun fire(event: MazeEvent, eventSinks: List<MazeEventSink>) {
        val terminator: (MazeEvent, List<MazeEventSink>) -> Unit = { ev, evSinks ->
            evSinks.forEach { it.onEvent(ev) }
        }

        if (filters.isNotEmpty()) {
            filters.first().filter(event, eventSinks, buildFilterChain(0, filters, terminator))
        } else {
            terminator(event, eventSinks)
        }

    }
}
