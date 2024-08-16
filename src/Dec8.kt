import java.io.File
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val title = "Advent of code December 8"
    val timeTaken = measureTime {

        val inputString = File("src/dec8.txt").readText()
        val lines = inputString.lines()
        val directionsString = lines[0]
        val nodes = (2 until lines.size).map {
            val line = lines[it]
            val split = line.split("=")
            val key = split[0].trim()
            val directions = split[1].split(",")
            val left = directions[0].filter { it.isLetter() || it.isDigit() }
            val right = directions[1].filter { it.isLetter() || it.isDigit() }
            key to (left to right)
        }.toMap()


        fun getNextKey(
            key: String,
            index: Int,
            stepsString: String,
            nodes: Map<String, Pair<String, String>>,
            setNextKeyAndIndex: (String, Int) -> Unit
        ) {
            val directions = nodes[key]!!
            val nextKey = when(stepsString[index]){
                'L' -> directions.first
                else -> directions.second
            }
            val nextIndex = if(index+1 < stepsString.length) index + 1 else 0
            setNextKeyAndIndex(nextKey, nextIndex)
        }

        var currentKey = "AAA"
        var steps = 0L
        var directionIndex = 0

        do {
            getNextKey(currentKey, directionIndex, directionsString, nodes){ s, i ->
                currentKey = s
                directionIndex = i
            }
            steps++
        } while (currentKey != "ZZZ")

        //Aswer one
        println(steps)
        assertEquals(19637, steps)


        val nodesWithAKey = nodes.filter { it.key.endsWith('A') }.keys
        val nodesWithAKeyStepsToZ = nodesWithAKey.map {
            var currentKey = it
            var steps = 0L
            var directionIndex = 0

            do {
                getNextKey(currentKey, directionIndex, directionsString, nodes){ s, i ->
                    currentKey = s
                    directionIndex = i
                }
                steps++
            } while (currentKey.endsWith('Z').not())

            steps
        }

        //https://stackoverflow.com/questions/4201860/how-to-find-gcd-lcm-on-a-set-of-numbers
        fun calculateLCM(numbers: List<Long>): Long {
            require(numbers.isNotEmpty()) { "List must not be empty" }
            fun calculateGCD(a: Long, b: Long): Long = if (b == 0L) abs(a) else calculateGCD(b, a % b)
            fun calculateLCMOfTwo(a: Long, b: Long) =
                if (a == 0L || b == 0L) 0L else abs(a * b) / calculateGCD(a, b)
            return numbers.reduce { acc, i -> calculateLCMOfTwo(acc, i) }
        }

        //Answer2
        println(nodesWithAKeyStepsToZ)
        assertEquals(8811050362409, calculateLCM(nodesWithAKeyStepsToZ))
    }
    println("\nTime to run \"$title\": $timeTaken")
}