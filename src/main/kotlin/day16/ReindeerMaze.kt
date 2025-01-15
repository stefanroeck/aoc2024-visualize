package day16

import util.MapOfThings.Direction
import util.MapOfThings.Point
import util.Maze
import util.MazeElement
import util.MazeEvent
import java.util.concurrent.atomic.AtomicInteger

private data class PointWithDirection(val point: Point, val direction: Direction)

private interface PathOptimizationStrategy {
    fun abortTraversal(pointWithDirection: PointWithDirection, currentCosts: Long): Boolean
}

private class CheckVisitedPointsForLowerCosts : PathOptimizationStrategy {
    private val visitedPointsWithLowestCosts = mutableMapOf<Point, Long>()
    override fun abortTraversal(pointWithDirection: PointWithDirection, currentCosts: Long): Boolean {
        val result = visitedPointsWithLowestCosts[pointWithDirection.point]?.let { previousCosts ->
            previousCosts < currentCosts
        } ?: false
        if (result) {
            return true
        }
        visitedPointsWithLowestCosts[pointWithDirection.point] = currentCosts
        return false
    }
}

private class CheckVisitedPointsWithDirectionForLowerCosts : PathOptimizationStrategy {
    private val visitedPointsWithLowestCosts = mutableMapOf<PointWithDirection, Long>()
    override fun abortTraversal(pointWithDirection: PointWithDirection, currentCosts: Long): Boolean {
        val result = visitedPointsWithLowestCosts[pointWithDirection]?.let { previousCosts ->
            // we've been here for lower costs already. if the costs are the same,
            // we continue as we need all paths
            previousCosts < currentCosts
        } ?: false
        if (result) {
            return true
        }
        visitedPointsWithLowestCosts[pointWithDirection] = currentCosts
        return false
    }
}

private class CombinedOptimizationStrategy(private val delegates: List<PathOptimizationStrategy>) :
    PathOptimizationStrategy {
    override fun abortTraversal(pointWithDirection: PointWithDirection, currentCosts: Long): Boolean {
        delegates.forEach { delegate ->
            if (delegate.abortTraversal(pointWithDirection, currentCosts)) {
                return true
            }
        }
        return false
    }

}

private class CheckForKnownTotalCosts(private val maximumCosts: Long) : PathOptimizationStrategy {
    override fun abortTraversal(pointWithDirection: PointWithDirection, currentCosts: Long): Boolean {
        return currentCosts > maximumCosts
    }
}


private data class PathThroughMaze(val path: List<Point>, val costs: Long)

typealias Costs = Int

interface DirectionStrategy {
    fun sortPossibleDirections(
        candidates: List<Triple<Direction, Point, Costs>>,
        currentDirection: Direction
    ): List<Triple<Direction, Point, Costs>>
}

private class LowestCostDirectionStrategy : DirectionStrategy {
    override fun sortPossibleDirections(
        candidates: List<Triple<Direction, Point, Costs>>,
        currentDirection: Direction
    ): List<Triple<Direction, Point, Costs>> {
        return candidates.sortedBy { it.third }
    }
}

enum class DirectionStrategies(val fn: DirectionStrategy) {
    LowestCost(LowestCostDirectionStrategy())
}

class ReindeerMaze(private val lines: List<String>, strategy: DirectionStrategies) {

    private val directionStrategy = strategy.fn

    val maze: Maze by lazy {
        Maze(lines)
    }

    fun shortestPathCost(): Long {
        maze.onEvent(MazeEvent.Start)

        val solvedPaths = mutableListOf<PathThroughMaze>()
        val steps = AtomicInteger(0)

        move(
            maze.startPosition,
            Direction.Right,
            CheckVisitedPointsForLowerCosts(),
            0,
            steps,
            solvedPaths,
            listOf(maze.startPosition)
        )

        maze.onEvent(MazeEvent.Finish(steps = steps.toLong()))

        return solvedPaths.minOfOrNull { it.costs } ?: 0
    }

    fun seatsOnShortestPaths(): Long {
        // determine lowest costs (part 1)
        val shortestPathCosts = shortestPathCost()

        // traverse again and visit all paths that result in lowest determined cost
        // also make sure to visit all the same points in all directions as this could result in
        // additional paths with the same costs
        val pathOptimizationStrategy = CombinedOptimizationStrategy(
            listOf(
                CheckVisitedPointsWithDirectionForLowerCosts(),
                CheckForKnownTotalCosts(shortestPathCosts),
            )
        )
        val solvedPaths = mutableListOf<PathThroughMaze>()
        val steps = AtomicInteger(0)

        move(
            maze.startPosition,
            Direction.Right,
            pathOptimizationStrategy,
            0,
            steps,
            solvedPaths,
            listOf(maze.startPosition)
        )

        val distinctPoints = solvedPaths
            .flatMap { it.path }
            .distinct()
        return distinctPoints.count().toLong()
    }

    private fun move(
        position: Point,
        direction: Direction,
        pathOptimizationStrategy: PathOptimizationStrategy,
        costs: Long,
        steps: AtomicInteger,
        solvedPathsCosts: MutableList<PathThroughMaze>,
        path: List<Point>
    ) {
        maze.onEvent(MazeEvent.Movement(position = position, costs = costs, steps = steps.toLong()))
        steps.incrementAndGet()

        if (maze.events.doCancel) {
            return
        }

        if (position == maze.endPosition) {
            maze.onEvent(MazeEvent.FoundSolution(path = path, costs = costs, steps = steps.toLong()))
            solvedPathsCosts.add(PathThroughMaze(path, costs))
            return
        }

        if (solvedPathsCosts.any { costs > it.costs }) {
            maze.onEvent(MazeEvent.AbandonPath)
            return // already found a cheaper way
        }

        if (pathOptimizationStrategy.abortTraversal(PointWithDirection(position, direction), costs)) {
            maze.onEvent(MazeEvent.AbandonPath)
            return
        }

        val possibleDirections = Direction.xyDirections()
            .filter { direction != it.inverse() } // no u-turns
            .map { it to position.translate(1, it) }
            .filter { maze.map.thingAt(it.second) != MazeElement.Wall } // don't run into walls

        if (possibleDirections.isEmpty()) {
            maze.onEvent(MazeEvent.AbandonPath)
            return // dead end
        }

        val sortedDirections =
            directionStrategy.sortPossibleDirections(possibleDirections.map<Pair<Direction, Point>, Triple<Direction, Point, Int>> {
                Triple(
                    it.first,
                    it.second,
                    costs(direction, it.first)
                )
            }, direction)

        sortedDirections
            .forEach { (direction, point, newCosts) ->
                move(
                    point,
                    direction,
                    pathOptimizationStrategy,
                    costs + newCosts,
                    steps,
                    solvedPathsCosts,
                    path + point
                )
            }
    }

    private fun costs(currentDirection: Direction, newDirection: Direction) =
        if (currentDirection == newDirection) 1 else 1001 // 1001: one for turning, +1 for step
}