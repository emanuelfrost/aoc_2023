import java.io.File
import kotlin.math.*
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class, ExperimentalStdlibApi::class)
fun main(args: Array<String>) {

    val title = "Advent of code December 11"
    val timeTaken = measureTime {

        val testData = """...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#....."""


        val inputString = File("src/dec11.txt").readText()
        val lines = inputString.lines()

        val galaxyChar = '#'
        data class Vec2(val x: Long, val y: Long){
            fun distanceTo(other: Vec2) =
                distance(this, other)

            fun manhattanDistanceTo(other: Vec2) =
                abs(this.x - other.x) + abs(this.y - other.y)

            private fun distance(a: Vec2, b: Vec2) : Double {
                val x = (a.x - b.x).absoluteValue
                val y = (a.y - b.y).absoluteValue
                return hypot(x.toDouble(), y.toDouble())
            }

            operator fun plus(other: Vec2): Vec2 {
                return Vec2(this.x + other.x, this.y + other.y)
            }
        }


        data class Galaxy(val id: Int, val pos: Vec2)

        val emptyColumns = lines.first().runningFoldIndexed(initial = 0L){ rowIndex, acc, _ ->
            val column = lines.map { it[rowIndex] }
            val empty = column.contains(galaxyChar).not()
            if(empty) acc + 1L else acc
        }.drop(1)

        val emptyRows = lines.runningFoldIndexed(initial = 0L){ _, acc, str ->
            val empty = str.contains(galaxyChar).not()
            if(empty) acc + 1L else acc
        }.drop(1)

        val galaxies = mutableListOf<Galaxy>()

        var galaxyId = 0
        lines.mapIndexed { lineIndex, lineString ->
            lineString.mapIndexed { charIndex, c ->
                if (c == galaxyChar) {
                    val foundGalaxy = Galaxy(
                        id = galaxyId,
                        pos = Vec2(charIndex.toLong(), lineIndex.toLong())
                    )
                    galaxies.add(foundGalaxy)
                    galaxyId++
                }
            }
        }

        val usedIndexesInPairs = mutableListOf<Int>()
        val galaxyPairIds = mutableListOf<Pair<Int, Int>>()
        galaxies.forEach { galaxy1 ->
            galaxies.forEach { galaxy2 ->
                if(galaxy2.id != galaxy1.id && galaxy2.id !in usedIndexesInPairs){
                    galaxyPairIds.add(Pair(galaxy1.id, galaxy2.id))
                }
            }
            usedIndexesInPairs += galaxy1.id
        }

        val sum = galaxyPairIds.sumOf {
            val galaxy1 = galaxies[it.first].pos
            val galaxy2 = galaxies[it.second].pos
            val v1 = galaxy1 + Vec2(emptyColumns[galaxy1.x.toInt()], emptyRows[galaxy1.y.toInt()])
            val v2 = galaxy2 + Vec2(emptyColumns[galaxy2.x.toInt()], emptyRows[galaxy2.y.toInt()])
            v1.manhattanDistanceTo(v2)
        }

        println(sum)
        //assertEquals(10228230, sum)


        val sum2 = galaxyPairIds.sumOf {
            val galaxy1 = galaxies[it.first].pos
            val galaxy2 = galaxies[it.second].pos
            val factor = 1000000L-1L
            val v1 = galaxy1 + Vec2(emptyColumns[galaxy1.x.toInt()] * factor, emptyRows[galaxy1.y.toInt()] * factor)
            val v2 = galaxy2 + Vec2(emptyColumns[galaxy2.x.toInt()] * factor, emptyRows[galaxy2.y.toInt()] * factor)
            v1.manhattanDistanceTo(v2)
        }

        println(sum2)
        assertEquals(447073334102, sum2)
    }

    println("\nTime to run \"$title\": $timeTaken")
}