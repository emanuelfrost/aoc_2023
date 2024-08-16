import java.io.File
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {

    val testData = """0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45"""

    val title = "Advent of code December 9"
    val timeTaken = measureTime {

        val inputString = File("src/dec9.txt").readText()
        val lines = inputString.lines()
        val histories = lines.map {
            it.split(" ").map {
                it.toLong()
            }
        }

        val sum = histories.fold(initial = 0L){acc, history ->
            acc + getNextNumberInHistory(history)
        }

        //First answer
        println(sum)
        assertEquals(1887980197, sum)

        val sum2 = histories.fold(initial = 0L){acc, history ->
            acc + getFirstNumberInHistory(history)
        }

        println(sum2)


    }
    println("\nTime to run \"$title\": $timeTaken")
}

fun getFirstNumberInHistory(history: List<Long>): Long {
    val historyWithDiffs = getHistoryWithDiffs(history)
    return calculateFirst(historyWithDiffs)
}

fun calculateFirst(historyWithDiffs: List<List<Long>>) : Long {
    val reversedHistory = historyWithDiffs.reversed()
    return reversedHistory.foldIndexed(initial = 0L){ index, acc, longs ->
        longs.first() - acc
    }
}

fun getNextNumberInHistory(history: List<Long>): Long {
    val historyWithDiffs = getHistoryWithDiffs(history)
    return calculateNext(historyWithDiffs)
}

fun getHistoryWithDiffs(history: List<Long>): List<List<Long>> {
    val historyWithDiffs = listOf(history).toMutableList()
    do{
        val diffs = historyWithDiffs.last().zipWithNext().map { it.second - it.first }
        historyWithDiffs.add(diffs)
    } while (historyWithDiffs.last().all { it == 0L }.not())

    return historyWithDiffs.toList()
}

fun calculateNext(historyWithDiffs: List<List<Long>>) : Long {
    val reversedHistory = historyWithDiffs.reversed()
    return reversedHistory.foldIndexed(initial = 0L){ index, acc, longs ->
        longs.last() + acc
    }
}
