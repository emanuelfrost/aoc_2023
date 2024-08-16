import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class, ExperimentalStdlibApi::class)
fun main(args: Array<String>) {

    val title = "Advent of code December 14"
    val input = File("src/dec14.txt").readText()

    val timeTaken = measureTime {
        val r = Rocks()

        val rows = r.tiltNorth(input.lines())

        r.debugPrint(rows)

        val score = rows.foldIndexed(initial = 0){ rowIndex, acc, s ->
            val count : Int = s.toList().count { it == 'O' }
            val p = rows.size-rowIndex
            acc + (count * p)
        }

        println(score)
        assertEquals(108641, score)

        val res = (0 .. 1000000000).fold(input.lines()){acc, cycle ->
            //println(cycle)
            val n = r.tiltNorth(acc)
            val w = r.tiltWest(n)
            val s = r.tiltSouth(w)
            val e = r.tiltEast(s)


            val score2 = e.foldIndexed(initial = 0){ rowIndex, acc, s ->
                val count : Int = s.toList().count { it == 'O' }
                val p = rows.size-rowIndex
                acc + (count * p)
            }

            println(score2)

            e
        }

        //println()
        //val res = r.tilt90CW(r.testData)
        //r.debugPrint(res.lines())

    }


    println("\nTime to run \"$title\": $timeTaken")

}

class Rocks{
    val testData = """O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#...."""

    fun debugPrint(lines: List<String>){
        lines.forEach(::println)
    }

    fun tiltEast(lines: List<String>) : List<String>{
        val ln = lines.map {
            val reversed = it.toList().reversed()
            rollRowToWest(reversed).joinToString("")
        }

        return ln.map { it.reversed() }
    }

    fun tiltWest(lines: List<String>) : List<String>{
        val ln = lines.map {
            rollRowToWest(it.toList()).joinToString("")
        }

        return ln
    }

    fun tiltNorth(lines: List<String>) : List<String>{
        val ln = (0..lines.first().lastIndex).map { colIndex ->
            val col = lines.map { it[colIndex] }
            rollRowToWest(col)
        }.map {
            it.joinToString("")
        }

        //Turn back to rows
        val rows = (0..ln.first().lastIndex).map { rowIndex ->
            ln.map { it[rowIndex] }.joinToString("")
        }

        return rows
    }


    fun tiltSouth(lines: List<String>) : List<String>{
        val ln = (0..lines.first().lastIndex).map { colIndex ->
            val col = lines.map { it[colIndex] }
            rollRowToWest(col.reversed()).reversed()
        }.map {
            it.joinToString("")
        }

        //Turn back to rows
        val rows = (0..ln.first().lastIndex).map { rowIndex ->
            ln.map { it[rowIndex] }.joinToString("")
        }

        return rows
    }

//    fun tiltEast(lines: List<String>) : List<String>{
//
//    }

    private fun rollRowToWest(line: List<Char>) : List<Char>{
        var lastFreeIndex : Int? = null
        return line.foldIndexed(initial = line) { index, acc, c ->
            when (c) {
                '.' -> lastFreeIndex = lastFreeIndex?.let { min(it, index) } ?: index
                '#' -> lastFreeIndex = null
                'O' -> {
                    lastFreeIndex?.let {
                        Collections.swap(acc, index, it)
                        lastFreeIndex = it+1
                    }
                }
            }

            acc
        }
    }

    //https://stackoverflow.com/questions/42519/how-do-you-rotate-a-two-dimensional-array
    fun tilt90CW(testData: String) {
        val charLines = testData.lines().map { it.toList() }
        val lists = zip(charLines)
        println(lists)
    }

    //https://stackoverflow.com/questions/50078266/zip-3-lists-of-equal-length
    //>>> list(zip(*[[1,2,3],[4,5,6],[7,8,9]]))
    //[[1,4,7],[2,5,8],[3,6,9]]
    public inline fun <T> zip(vararg lists: List<T>): List<List<T>> {
        return zip(*lists, transform = { it })
    }

    public inline fun <T, V> zip(vararg lists: List<T>, transform: (List<T>) -> V): List<V>{
        val minSize = lists.minOfOrNull(List<T>::size) ?: return emptyList()
        val list = ArrayList<V>(minSize)

        val iterators = lists.map { it.iterator() }
        var i = 0
        while (i < minSize) {
            list.add(transform(iterators.map { it.next() }))
            i++
        }
        return list
    }
}