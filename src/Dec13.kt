import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class, ExperimentalStdlibApi::class)
fun main(args: Array<String>) {

    val title = "Advent of code December 13"
    val timeTaken = measureTime {
        val input = File("src/dec13.txt").readText()


        val mirror = Mirror()
        val lines = mirror.getMirrors(input)

        val r = lines.sumOf { line ->
            val findRowMirror = mirror.findRowMirror(line)
            val rows = findRowMirror.firstOrNull { it.second }?.let { it.first+1 } ?: 0
            val findColumnMirror = mirror.findColumnMirror(line)
            val columns = findColumnMirror.firstOrNull { it.second }?.let { it.first+1 } ?: 0


            if(findRowMirror.filter { it.second }.size > 1){
                println("------------------------")
            }
            if(findColumnMirror.filter { it.second }.size > 1){
                println("------------------------")
            }

            (rows*100) + (columns)
        }

        println(r)

        val t = """..###..##..
.#.########
.###....###
..#..#####.
####...#.##
####...#.##
..#..#####.
.###....###
.#.########
..###...#..
##..#...#..
.#.....#..#
########..#
.#.##.#....
.#.##.#....
########..#
.#.....#..#"""

        val row = mirror.findRowMirror(mirror.testDataRowsMirror.lines()).firstOrNull { it.second }?.let { it.first+1 } ?: 0
        val cols = mirror.findColumnMirror(mirror.testDataColumnMirror.lines()).firstOrNull { it.second }?.let { it.first+1 } ?: 0

        println("${row*100} ${cols}")

    }

    println("\nTime to run \"$title\": $timeTaken")
}

class Mirror(){
    fun getMirrors(input: String) : List<List<String>>{
        return sequence {
            val newLine = mutableListOf<String>()
            input.lines().forEach {
                if(it.isEmpty())
                {
                    yield(newLine.toList())
                    newLine.clear()
                }
                else {
                    newLine.add(it.trim())
                }
            }
        }.toList()
    }

    val testDataRowsMirror = """#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#"""

    val testDataColumnMirror = """#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#."""

    fun findRowMirror(lines: List<String>) : List<Pair<Int, Boolean>>{
        return (0 until lines.lastIndex).map { index ->
            val t1 = lines.subList(0, index + 1)
            //t1.forEach(::println)
            //println("--")
            val t2 = lines.subList(index + 1, lines.size)
            //t2.forEach(::println)
            //println()

            val score1 = t2.mapIndexedNotNull{ i, s ->
                val rowsEqual = s == t1.getOrNull(t1.lastIndex - i)
                if(rowsEqual) null else s
            }

            val score2 = t1.mapIndexedNotNull{ i, s ->
                if(s == t2.getOrNull(t1.size-1-i)) null else s
            }

//            println(score1.size)
//            println(score2.size)
//            println()
//            println()
//            println()
            Pair(index, score1.isEmpty() || score2.isEmpty())
        }
    }

    fun findColumnMirror(lines: List<String>) : List<Pair<Int, Boolean>>{
        val columns = lines.first().indices.map { colIndex -> lines.map { it[colIndex] }.joinToString("") }
        return findRowMirror(columns)
    }

    fun comp(lines: List<String>){
        for(i in 0 until lines.size){
            println(i+1)

            //val t1 = (0 until lines.size-i).map { lines[it] }
            val t1 = lines.subList(0, lines.size-i)
            t1.forEach(::println)

            println()
            println()
            val tv = lines.subList(lines.size-i, lines.size).reversed()
            //val t2 = (lines.size-1 downTo lines.size-i).map { lines[it] }
            tv.forEach(::println)

            println()

//            val similar = (tv.indices).fold(initial = 0){ acc, ri ->
//                acc + if(tv[tv.lastIndex - ri] == t1.getOrNull(t1.lastIndex - ri)) 1 else 0
//            }

            val similar = (0 until i).fold(initial = 0){ acc, ri ->
                val backIndex = 1 + i + ri
                val topIndex = i - ri

                val backLine = lines.getOrNull(backIndex)
                val topLine = lines.getOrNull(topIndex)

                acc + if(backLine?.let { it == topLine } == true) 1 else 0
            }

            println("Similar $similar")

            println()
            println()
        }
    }

    fun findVerticalMirrorIndex(lines: List<String>) : Int? {
        return lines.first().withIndex().maxByOrNull { item ->
            checkVerticalReflectionValue(item.index, lines)
        }?.index
    }

    private fun List<String>.getColumnOrNull(index: Int) =
        if (index >= 0 && index <= this.first().lastIndex) this.map { it[index] } else null

    private fun checkVerticalReflectionValue(columIndex: Int, lines: List<String>) : Int {
        return (0 until columIndex).fold(initial = 0){ acc, i ->
            val backIndex = 1 + columIndex + i
            val topIndex = columIndex - i

            val backColumn = lines.getColumnOrNull(backIndex)
            val frontColumn = lines.getColumnOrNull(topIndex)

            acc + if(backColumn?.let { it == frontColumn } == true) 1 else 0
        }
    }

    fun findHorizontalMirrorIndex(lines: List<String>) : Int? {
        val scores = lines.indices.map { index ->
            checkHorizontalReflectionValue(index, lines)
        }

        lines.indices.forEach{
            debugPrintHorizontal(lines, scores[it], it)
        }

        return scores.withIndex().maxByOrNull { it.value }?.index
    }

    private fun debugPrintHorizontal(
        lines: List<String>,
        score: Int,
        index:Int
    ) {
        println()
        println()
        println("Index: $index")
        val t1 = lines.subList(0, lines.size - index)
        t1.forEach(::println)
        println("--")
        val t2 = lines.subList(lines.size - index, lines.size).reversed()
        t2.forEach(::println)
        println("Score: $score")
    }

    private fun checkHorizontalReflectionValue(rowIndex: Int, lines: List<String>) : Int {
        return (0 until rowIndex).fold(initial = 0){ acc, i ->
            val backIndex = 1 + rowIndex + i
            val topIndex = rowIndex - i

            val backLine = lines.getOrNull(backIndex)
            val topLine = lines.getOrNull(topIndex)

            acc + if(backLine?.let { it == topLine } == true) 1 else 0
        }
    }
}
