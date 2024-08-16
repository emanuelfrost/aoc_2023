import kotlin.test.assertEquals

fun main(args: Array<String>) {
    val dec6 = Dec6()

    val races = dec6.input.lines()
    val times = races[0].split(" ").mapNotNull { it.filter { it.isDigit() }.toLongOrNull() }
    val distance = races[1].split(" ").mapNotNull { it.filter { it.isDigit() }.toLongOrNull() }
    val competitions = times.zip(distance)

    fun possibleWins(competitionTime: Long, distanceRecord: Long) : Long{
         return (0..competitionTime).fold(initial = 0){possibleWins, mmPerSecond ->
            val timeToTravel = competitionTime - mmPerSecond
            val travelDistance = mmPerSecond * timeToTravel
            when {
                travelDistance > distanceRecord -> possibleWins +1
                else -> possibleWins
            }
        }
    }

    val possibleWinsMultipliedForAllCompetitions = competitions.fold(initial = 0L){ acc, pair ->
        val (competitionTime, distanceRecord) = pair

        val possibleWins = possibleWins(competitionTime, distanceRecord)

        if(acc == 0L)
            possibleWins
        else
            acc*possibleWins
    }

    //Answer 1
    println(possibleWinsMultipliedForAllCompetitions)
    assertEquals(1083852, possibleWinsMultipliedForAllCompetitions)


    val competitionTime = races[0].filter { it.isDigit() }.toLongOrNull() ?: 0
    val record = races[1].filter { it.isDigit() }.toLongOrNull() ?: 0

    val possibleWinsForSIngleCompetition = possibleWins(competitionTime, record)

    //Answer 2
    println(possibleWinsForSIngleCompetition)
    assertEquals(23501589, possibleWinsForSIngleCompetition)
}
class Dec6 {
    val input ="""Time:        38     94     79     70
Distance:   241   1549   1074   1091"""
}