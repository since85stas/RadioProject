package stas.batura.radioproject.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.exoplayer2.Timeline
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import stas.batura.radioproject.data.dataUtils.DateTime
import stas.batura.radioproject.data.dataUtils.TIME_WEEK
import stas.batura.radioproject.data.net.PodcastBody
import stas.batura.radioproject.data.net.TimeLabel


@Entity(tableName = "podcast_table")
data class Podcast(
    @PrimaryKey()
    val podcastId: Int ,

    // url поста
    val url: String =     "url",

    // заголовок поста
    val title: String  =   "title",

    // дата-время поста в RFC3339
    val time: String     = "0",

    val categories: List<String>? = null,

    var imageUrl: String? = null,

    var fileName:   String? = null,

    var bodyHtml: String? = null,

    var postText: String? = null,

    var audioUrl: String? = null,

    var timeLabels: List<TimeLabel>? = null ,

    var isActive: Boolean = false,

    var isFinish: Boolean = false,

    var lastPosition: Long = 0

//    var localImageUrl: String? = null
) {


    object FromPodcastBody  {

        fun build(podcastBody: PodcastBody): Podcast {

            // убираем все буквы, оставляем только номер
            val reg = "\\D".toRegex()
            val num = reg.replace(podcastBody.title, "")

            return Podcast(num.toInt() ,
                podcastBody.url,
                podcastBody.title,
                podcastBody.date.toString(),
                podcastBody.categories,
                podcastBody.imageUrl,
                podcastBody.fileName,
                podcastBody.bodyHtml,
                podcastBody.postText,
                podcastBody.audioUrl,
                fillTimelable(podcastBody.timeLables)
            )
        }

    }

    /**
     * check if week is passed after [newTime] value
     */
    fun isWeekGone(newTime: Long): Boolean {
                if (newTime - getMillisTime() > TIME_WEEK) {
                    return true
                } else {
                    return false
                }
    }


    /**
     * transform class field [time] to Milliseconds
     */
    fun getMillisTime(): Long {
        val dateTime: DateTime = DateTime.parseRfc3339(time)
        val millis: Long = dateTime.getValue()
        return millis
    }
}

class CategoryDataConverter {

        @TypeConverter()
        fun fromCountryLangList(value: List<String>): String {
            val gson = Gson()
            val type = object : TypeToken<List<String>>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        fun toCountryLangList(value: String): List<String> {
            val gson = Gson()
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(value, type)
        }

}



