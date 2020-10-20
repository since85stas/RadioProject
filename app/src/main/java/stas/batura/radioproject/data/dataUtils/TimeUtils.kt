package stas.batura.radioproject.data.dataUtils

val TIME_WEEK = 7*24*60*60*1000L

/**
 * transform class field [time] to Milliseconds
 */
fun getMillisTime(time: String): Long {
    val dateTime: DateTime = DateTime.parseRfc3339(time)
    val millis: Long = dateTime.getValue()
    return millis
}

enum class Year {
    Y2020 = 2020

}