package stas.batura.radioproject.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import stas.batura.radioproject.data.dataUtils.DateTime
import stas.batura.radioproject.data.dataUtils.TIME_WEEK
import stas.batura.radioproject.data.net.PodcastBody

@Entity(tableName = "podcast_table")
data class Podcast(
    @PrimaryKey()
    val podcastId: Int ,

    // url поста
    val url: String =     "url",

    // заголовок поста
    val title: String  =   "title",

    // дата-время поста в RFC3339
    val time: String     = "0"
) {


    object FromPodcastBody  {

        fun build(podcastBody: PodcastBody): Podcast {

            // убираем все буквы, оставляем только номер
            val reg = "\\D".toRegex()
            val num = reg.replace(podcastBody.title, "")

            return Podcast(num.toInt() ,podcastBody.url, podcastBody.title, podcastBody.date.toString())
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

