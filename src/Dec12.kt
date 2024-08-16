import java.io.File
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class, ExperimentalStdlibApi::class)
fun main(args: Array<String>) {

    val title = "Advent of code December 12"
    val timeTaken = measureTime {

        val springCalc = SpringCalc()

        val testData = """???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1"""

        val expected = """.###.##.#...
.###.##..#..
.###.##...#.
.###.##....#
.###..##.#..
.###..##..#.
.###..##...#
.###...##.#.
.###...##..#
.###....##.#"""

        val readLines = File("src/dec12.txt").readLines()
        val springs = readLines.map {
            springCalc.getSpring(it)
        }.forEach {
            springCalc.permutations(it.input).flatten()
            println("Done")
        }

        println(springs)
    }

    println("\nTime to run \"$title\": $timeTaken")
}

data class Spring(val str: String, val numbers: List<Int>){

    val springs = numbers.map { number ->
        buildString {
            for (i in 0 until number)
            {
                append("#")
            }
        }
    }

    val rest = numbers.fold(initial = str){ acc, i ->
        (0 until i).fold(initial = acc){ acc2, i2 ->
            if(acc2.contains("#"))
                acc2.replaceFirst("#", "")
            else
                acc2.replaceFirst("?", "")
        }
    }.map { it.toString() }

    val input: List<String> = springs + rest
}

class SpringCalc {
    //https://medium.com/@jcamilorada/recursive-permutations-calculation-algorithm-in-kotlin-86233a0a2ee1
    fun permutations(input: List<String>): MutableSet<List<String>> {
        val solutions = mutableSetOf<List<String>>()
        val cache = mutableSetOf<Pair<Int, List<String>>>()
        permutationsRecursive(input, 0, 0, cache, solutions)
        return solutions
    }

    fun permutationsRecursive(input: List<String>, index: Int, originalIndex: Int, cache: MutableSet<Pair<Int,List<String>>>, answers: MutableSet<List<String>>) {
        if(index == input.lastIndex) {
            answers.add(input.toList())
        }

        //create cache as a map, where keys are sets of numbers and values are results of function call

        for (i in index..input.lastIndex) {
                Collections.swap(input, index, i)

            //if(!cache.contains(index+1 to input)) {
                permutationsRecursive(input, index + 1, index, cache, answers)
            //}
                Collections.swap(input, i, index)
        }
    }

    fun getSpring(testData: String): Spring {
        val row = testData.split(" ")
        val str = row[0]
        val numbers = row[1].split(",").map { it.toInt() }
//
//        val springCount = testData.filter { it == '#' }.count()
//        val sum = numbers.sum()
//        val springsToAdd = sum-springCount
//

        return Spring(str, numbers)
    }

    fun asSpringSet(strSpringPositions: String) : Set<Int> {
        return strSpringPositions.mapIndexedNotNull{ index, c ->
            if(c == '#')
                index
            else
                null
        }.toSet()
    }

    fun checkMatchingWithOriginal(strSpringPositions: Set<Int>, originalSpringPositions: Set<Int>) : Boolean {
        return strSpringPositions.containsAll(originalSpringPositions)
    }

    fun verifyGroups(str: String, numbers: List<Int>) : Boolean {
        val g = getGroups(str)
        return (g.size <= numbers.size) && g.withIndex().all { indexedValue ->
            indexedValue.value == numbers[indexedValue.index]
        }
    }

    fun verifyMaxGroupCount(str: String, numbers: List<Int>) : Boolean {
        val g = getGroups(str)
        val maxAllowedGroupSize = numbers.maxOrNull() ?: 0
        return g.all { it <= maxAllowedGroupSize }
    }

    fun getGroups(str: String, replaceChar: Char = '.', splitChar: Char = '?'): List<Int> {
        val split = str.replace(replaceChar, splitChar).split(splitChar).filter { it.isNotEmpty() }
        return split.map { it.length }
    }

    fun verifySequence(row: String, numbers: List<Int>) : Boolean{
        val len = getGroups(row, '.')
        return len == numbers
    }

}
