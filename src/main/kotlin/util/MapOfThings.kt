package util

import java.util.EnumSet
import kotlin.math.sign

data class MapOfThings<T>(private val points: Map<Point, T>, val width: Int, val height: Int) {

    enum class Direction {
        Left, Right, Up, Down, TopRight, BottomRight, BottomLeft, TopLeft;

        fun isHorizontal() = horizontalDirections().contains(this)
        fun isVertical() = verticalDirections().contains(this)
        fun inverse() = when (this) {
            Left -> Right
            Right -> Left
            Up -> Down
            Down -> Up
            TopRight -> BottomLeft
            BottomRight -> TopLeft
            BottomLeft -> TopRight
            TopLeft -> BottomRight
        }

        companion object {
            fun horizontalDirections() = EnumSet.of(Left, Right)
            fun verticalDirections() = EnumSet.of(Up, Down)
            fun xyDirections() = horizontalDirections() + verticalDirections()
        }
    }

    data class Vector(val dx: Long, val dy: Long) {
        constructor(dx: Int, dy: Int) : this(dx.toLong(), dy.toLong())

        /** Returns the Greatest Common Divisor */
        private fun gcd(a: Long, b: Long): Long {
            return if (b == 0L) a else gcd(b, a % b)
        }

        fun invert() = Vector(dx = this.dx * -1, dy = this.dy * -1)
        fun reduce(): Vector {
            val gcd = gcd(dx, dy)
            return if (gcd == 1L) this else Vector(dx / gcd, dy / gcd)

        }
    }

    data class Point(val col: Long, val row: Long) : Comparable<Point> {
        constructor(col: Int, row: Int) : this(col.toLong(), row.toLong())

        companion object {
            fun pointsBetween(start: Point, end: Point): List<Point> {
                val points = mutableListOf<Point>()
                val deltaCol = sign((end.col - start.col).toDouble()).toInt()
                val deltaRow = sign((end.row - start.row).toDouble()).toInt()

                var nextPoint = start
                do {
                    points.add(nextPoint)
                    nextPoint = Point(col = nextPoint.col + deltaCol, row = nextPoint.row + deltaRow)
                } while (nextPoint != end)

                points.add(end)
                return points.toList()
            }

            fun gradient(start: Point, end: Point): Double {
                return (end.row - start.row).toDouble() / (end.col - start.col).toDouble()
            }

            fun vector(start: Point, end: Point): Vector = Vector(dx = end.col - start.col, dy = end.row - start.row)
        }

        fun translate(delta: Int, direction: Direction): Point {
            return when (direction) {
                Direction.Left -> Point(this.col - delta, this.row)
                Direction.Right -> Point(this.col + delta, this.row)
                Direction.Up -> Point(this.col, this.row - delta)
                Direction.Down -> Point(this.col, this.row + delta)
                Direction.TopRight -> Point(this.col + delta, this.row - delta)
                Direction.BottomRight -> Point(this.col + delta, this.row + delta)
                Direction.BottomLeft -> Point(this.col - delta, this.row + delta)
                Direction.TopLeft -> Point(this.col - delta, this.row - delta)
            }
        }

        fun translate(vector: Vector) = Point(col + vector.dx, row + vector.dy)

        infix fun <T> within(map: MapOfThings<T>): Boolean {
            return col >= 0 && this.col < map.width && row >= 0 && this.row < map.height
        }

        fun within(topLeft: Point, bottomRight: Point): Boolean {
            return col >= topLeft.col && this.col <= bottomRight.col && row >= topLeft.row && this.row <= bottomRight.row
        }

        infix fun <T> outside(map: MapOfThings<T>): Boolean {
            return !within(map)
        }

        override fun compareTo(other: Point): Int {
            return if (row != other.row) row.compareTo(other.row) else (col.compareTo(other.col))
        }

        fun translateWithinBounds(vector: Vector, width: Long, height: Long) =
            Point(
                addWithOverflow(col, vector.dx, width),
                addWithOverflow(row, vector.dy, height)
            )

        private fun addWithOverflow(old: Long, delta: Long, max: Long): Long {
            check(delta < max) { "Multi line wrap not supported" }
            val new = old + delta
            return if (new >= max) {
                new % max
            } else if (new < 0) {
                max + new
            } else new
        }
    }

    companion object {
        fun <T> parse(rows: List<String>, mapper: (c: Char) -> T): MapOfThings<T> {
            val pointMap = rows
                .flatMapIndexed { row, letters ->
                    letters.mapIndexed { col, char -> Point(col, row) to mapper(char) }
                }.toMap()

            return MapOfThings(pointMap, height = rows.size, width = rows[0].length)
        }
    }

    fun thingAt(point: Point): T? {
        return points[point]
    }

    fun pointCount() = points.size

    fun points() = points.keys

    fun updatedMap(updater: (mutablePointMap: MutableMap<Point, T>) -> Unit): MapOfThings<T> {
        with(points.toMutableMap()) {
            updater.invoke(this)
            return copy(points = this)
        }
    }

    fun pointsFor(thing: T): Set<Point> = points.filter { it.value == thing }.keys

    fun adjacentPoints(
        point: Point,
        directions: Set<Direction> = Direction.xyDirections()
    ): Set<Point> = directions.map { point.translate(1, it) }.filter { it within this }.toSet()


}