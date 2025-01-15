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

enum class SolutionStrategy(val fn: DirectionStrategy, val label: String) {
    LowestCost(LowestCostDirectionStrategy(), "Lowest Costs"),
    ShortestDistance(ShortestDistanceToTarget(), "Shortest Distance"),
    LongestDistance(LongestDistanceToTarget(), "Longest Distance"),
}