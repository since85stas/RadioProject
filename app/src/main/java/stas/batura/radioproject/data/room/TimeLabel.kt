package stas.batura.radioproject.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import stas.batura.radioproject.data.net.TimeLabel
import java.util.regex.Pattern

/** Regular expression for parsing RFC3339 date/times.  */
private val PATTERN = Pattern.compile(
            "((\\d{2}):(\\d{2}):(\\d{2}))" // 'T'HH:mm:ss.milliseconds
) // 'Z' or time zone shift HH:mm following '+' or '-'


fun fillTimelable(timelables: List<TimeLabel>?): List<TimeLabel>? {

    var newTimeLables: MutableList<TimeLabel>? = mutableListOf()

    if (timelables != null) {
        var start = 0L
        // using regex to find start time
        val startTime: String = timelables[0].startTime
        val regex = PATTERN.toRegex()
        val res = regex.find(startTime)!!.value

        val digits = res.split(":")
        if (digits.size > 2 ) {
            start = (digits[1].toInt() * 60 + digits[2].toInt()) * 1000L
        }

        for (lable in timelables) {
            var newLable = lable
            newLable.newStartTime = start
            if (lable.duration != null) {
                start = start + lable.duration * 1000L
            }
            newTimeLables!!.add(newLable)
        }
        return newTimeLables
    } else {
        return null
    }

}

class TimeLabelsDataConverter {

    @TypeConverter()
    fun fromTimeLableList(value: List<TimeLabel>): String {
        val gson = Gson()
        val type = object : TypeToken<List<TimeLabel>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toTimeLableList(value: String): List<TimeLabel> {
        val gson = Gson()
        val type = object : TypeToken<List<TimeLabel>>() {}.type
        return gson.fromJson(value, type)
    }

}