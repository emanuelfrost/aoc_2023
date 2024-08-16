import java.io.File
import java.lang.Long.max
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class, ExperimentalStdlibApi::class)
fun main(args: Array<String>) {

    val title = "Advent of code December 10"
    val timeTaken = measureTime {

        val testData = """FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L"""


        val inputString = File("src/dec10.txt").readText()
        val lines = inputString.lines()

        lateinit var startPosition: Pos
        val pipeMap = lines.mapIndexed { lineIndex, line ->
            lineIndex to line.mapIndexed { charIndex, char ->
                val pos = Pos(charIndex, lineIndex, char)
                if (char == 'S')
                    startPosition = pos
                charIndex to pos
            }.toMap()
        }.toMap()

        val stepCalculator = StepCalculator(pipeMap)

        val startPos = listOfNotNull(
            stepCalculator.north(startPosition)?.let { Step(it, Direction.SOUTH, 1) },
            stepCalculator.east(startPosition)?.let { Step(it, Direction.WEST, 1) },
            stepCalculator.south(startPosition)?.let { Step(it, Direction.NORTH, 1) },
            stepCalculator.west(startPosition)?.let { Step(it, Direction.EAST, 1) },
        )

        val queue: Queue<Step> = LinkedList()
        queue.addAll(startPos)

        val mainLoopPositions = mutableSetOf(startPosition)

        var len = 0L
        do {
            val step = queue.poll()
            mainLoopPositions.add(step.position)
            val nextStep = stepCalculator.getDirection(step)

            nextStep?.let {
                if (nextStep.position.c == 'S') {
                    println("Loop end")
                    len = max(len, step.stepsTakenFromStart + 1)
                } else {
                    queue.add(nextStep)
                }
            } ?: run {
                len = max(len, step.stepsTakenFromStart)
            }
        } while (queue.isNotEmpty())

        //Answer 1
        val res = len / 2
        println(res)
        assertEquals(6897, res)

        val allPositions = pipeMap.map { it.value.values }.flatten().toMutableSet()
        val allPositionsWithoutMainLoop = allPositions.filter { mainLoopPositions.contains(it).not() }.toMutableList()

        var insideCounter = 0L
        allPositionsWithoutMainLoop.forEach { positionToCheckIfInside ->
            val row = pipeMap[positionToCheckIfInside.y]!!

            //Ray cast from position to end of row and check main loop edges hit on the way
            var edgeCount = 0
            (positionToCheckIfInside.x until row.size).forEach { rowIndex ->
                val rayPos = row[rowIndex]!!
                if (mainLoopPositions.contains(rayPos) && rayPos.c !in setOf('-', 'L', 'J')) {
                    edgeCount++
                }
            }

            //If odd
            insideCounter += if (edgeCount % 2 == 1) 1 else 0
        }

        //Answer 2
        println(insideCounter)
        assertEquals(367, insideCounter)
    }
    println("\nTime to run \"$title\": $timeTaken")
}

class StepCalculator(private val pipeMap: Map<Int, Map<Int, Pos>>) {
    private val validNorth = setOf('F', '7', '|', 'S')
    private val validEast = setOf('J', '7', '-', 'S')
    private val validSouth = setOf('J', 'L', '|', 'S')
    private val validWest = setOf('L', 'F', '-', 'S')

    private fun Pos.isValid(validChars: Set<Char>) = if (validChars.contains(this.c)) this else null
    private fun Pos.validNorth() = isValid(validNorth)
    private fun Pos.validEast() = isValid(validEast)
    private fun Pos.validSouth() = isValid(validSouth)
    private fun Pos.validWest() = isValid(validWest)

    fun north(pos: Pos) = pipeMap[pos.y - 1]?.get(pos.x)?.validNorth()
    fun east(pos: Pos) = pipeMap[pos.y]?.get(pos.x + 1)?.validEast()
    fun south(pos: Pos) = pipeMap[pos.y + 1]?.get(pos.x)?.validSouth()
    fun west(pos: Pos) = pipeMap[pos.y]?.get(pos.x - 1)?.validWest()

    fun getDirection(step: Step): Step? {
        return when (step.position.c) {
            '|' -> when (step.enteredFrom) {
                Direction.SOUTH -> north(step.position)?.let { Step(it, Direction.SOUTH, step.stepsTakenFromStart + 1) }
                Direction.NORTH -> south(step.position)?.let { Step(it, Direction.NORTH, step.stepsTakenFromStart + 1) }
                else -> null
            }
            '-' -> when (step.enteredFrom) {
                Direction.WEST -> east(step.position)?.let { Step(it, Direction.WEST, step.stepsTakenFromStart + 1) }
                Direction.EAST -> west(step.position)?.let { Step(it, Direction.EAST, step.stepsTakenFromStart + 1) }
                else -> null
            }
            'L' -> when (step.enteredFrom) {
                Direction.EAST -> north(step.position)?.let { Step(it, Direction.SOUTH, step.stepsTakenFromStart + 1) }
                Direction.NORTH -> east(step.position)?.let { Step(it, Direction.WEST, step.stepsTakenFromStart + 1) }
                else -> null
            }
            'J' -> when (step.enteredFrom) {
                Direction.NORTH -> west(step.position)?.let { Step(it, Direction.EAST, step.stepsTakenFromStart + 1) }
                Direction.WEST -> north(step.position)?.let { Step(it, Direction.SOUTH, step.stepsTakenFromStart + 1) }
                else -> null
            }
            '7' -> when (step.enteredFrom) {
                Direction.WEST -> south(step.position)?.let { Step(it, Direction.NORTH, step.stepsTakenFromStart + 1) }
                Direction.SOUTH -> west(step.position)?.let { Step(it, Direction.EAST, step.stepsTakenFromStart + 1) }
                else -> null
            }
            'F' -> when (step.enteredFrom) {
                Direction.EAST -> south(step.position)?.let { Step(it, Direction.NORTH, step.stepsTakenFromStart + 1) }
                Direction.SOUTH -> east(step.position)?.let { Step(it, Direction.WEST, step.stepsTakenFromStart + 1) }
                else -> null
            }
            else -> null
        }
    }
}

data class Step(
    val position: Pos,
    val enteredFrom: Direction,
    val stepsTakenFromStart: Long
)

data class Pos(val x: Int, val y: Int, val c: Char)

enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST
}