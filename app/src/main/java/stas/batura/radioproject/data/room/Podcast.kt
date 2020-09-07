package stas.batura.radioproject.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import stas.batura.radioproject.data.dataUtils.TIME_WEEK
import stas.batura.radioproject.data.net.PodcastBody

@Entity(tableName = "podcast_table")
data class Podcast(
    @PrimaryKey()
    var podcastId: Int ,

    // url поста
    var url: String =     "url",

    // заголовок поста
    var title: String  =   "title",

    // дата-время поста в RFC3339
    var time: String     = "0"
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
     * check if week is passed after [currTime] value
     */
    fun isWeekGone(currTime: Long): Boolean {
                if (getMillisTime() > TIME_WEEK) {
                    return true
                } else {
                    return false
                }
    }

    /**
     * transform class field [time] to Milliseconds
     */
    private fun getMillisTime(): Long {
        return 0L;

    }
}

