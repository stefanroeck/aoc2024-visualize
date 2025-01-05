package util

import util.MapOfThings.Point
import util.MazeElement.Empty
import util.MazeElement.End
import util.MazeElement.Start
import util.MazeElement.Wall

enum class MazeElement {
    Wall, Start, End, Empty
}

typealias MazeMap = MapOfThings<MazeElement>

class Maze(private val lines: List<String>, val events: MazeEvents = MazeEvents()) {

    private var currentPosition: Point? = null

    val map: MazeMap by lazy {
        MapOfThings.parse(lines) { c ->
            when (c) {
                '#' -> Wall
                'S' -> Start
                'E' -> End
                '.' -> Empty
                else -> {
                    throw IllegalArgumentException("Unknown char: $c")
                }
            }
        }
    }

    val startPosition by lazy { map.pointsFor(Start).single() }

    val endPosition by lazy {
        map.pointsFor(End).single()
    }

    fun onEvent(event: MazeEvent, context: String = "") {
        events.fire(event)

        when (event) {
            MazeEvent.Abort -> {}
            MazeEvent.FoundSolution -> println("Found solution at $currentPosition $context".trim())
            is MazeEvent.Movement -> currentPosition = event.position
        }
    }

}