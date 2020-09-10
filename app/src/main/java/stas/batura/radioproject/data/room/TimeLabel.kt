package stas.batura.radioproject.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import stas.batura.radioproject.data.net.TimeLabel


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