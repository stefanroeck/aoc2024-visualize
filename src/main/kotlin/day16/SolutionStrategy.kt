package day16

import util.MapOfThings.Direction
import util.MapOfThings.Point

typealias Costs = Int

interface DirectionStrategy {
    fun sortPossibleDirections(
        candidates: List<Triple<Direction, Point, Costs>>,
        currentDirection: Direction,
        endPosition: Point,
    ): List<Triple<Direction, Point, Costs>>
}

private class LowestCostDirectionStrategy : DirectionStrategy {
    override fun sortPossibleDirections(
        candidates: List<Triple<Direction, Point, Costs>>,
        currentDirection: Direction,
        endPosition: Point
    ): List<Triple<Direction, Point, Costs>> {
        return candidates.sortedBy { it.third }
    }
}

private class ShortestDistanceToTarget : DirectionStrategy {
    override fun sortPossibleDirections(
        candidates: List<Triple<Direction, Point, Costs>>,
        currentDirection: Direction,
        endPosition: Point
    ): List<Triple<Direction, Point, Costs>> {
        return candidates.sortedBy { Point.distance(it.second, endPosition) }
    }
}

private class LongestDistanceToTarget : DirectionStrategy {
    override fun sortPossibleDirections(
        candidates: List<Triple<Direction, Point, Costs>>,
        currentDirection: Direction,
        endPosition: Point
    ): List<Triple<Direction, Point, Costs>> {
        return candidates.sortedByDescending { Point.distance(it.second, endPosition) }
    }
}

enum class HandDirection {
    Left, Right,
}

private class HandOnWall(private val handDirection: HandDirection) : DirectionStrategy {
    companion object {
        private val sortedDirections = listOf(Direction.Up, Direction.Right, Direction.Down, Direction.Left)
        private fun Direction.turnRight() = sortedDirections[(sortedDirections.indexOf(this) + 1).mod(4)]
        private fun Direction.turnLeft() = sortedDirections[(sortedDirections.indexOf(this) - 1).mod(4)]
    }

    override fun sortPossibleDirections(
        candidates: List<Triple<Direction, Point, Costs>>,
        currentDirection: Direction,
        endPosition: Point
    ): List<Triple<Direction, Point, Costs>> {
        val preferredDirections = when (handDirection) {
            HandDirection.Left -> listOf(
                currentDirection.turnLeft(),
                currentDirection,
                currentDirection.turnRight(),
            )

            HandDirection.Right -> listOf(
                currentDirection.turnRight(),
                currentDirection,
                currentDirection.turnLeft(),
            )

        }
        return preferredDirections.mapNotNull { pref ->
            candidates.firstOrNull { it.first == pref }
        }
    }
}

enum class SolutionStrategy(val fn: DirectionStrategy, val label: String) {
    LowestCost(LowestCostDirectionStrategy(), "Lowest Costs"),
    ShortestDistance(ShortestDistanceToTarget(), "Shortest Distance"),
    LongestDistance(LongestDistanceToTarget(), "Longest Distance"),
    HandOnLeftWall(HandOnWall(HandDirection.Left), "Hand on left Wall"),
    HandOnRightWall(HandOnWall(HandDirection.Right), "Hand on right Wall"),
}