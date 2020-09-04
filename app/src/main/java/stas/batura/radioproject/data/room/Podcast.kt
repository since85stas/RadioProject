package stas.batura.radioproject.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import stas.batura.radioproject.data.net.PodcastBody

@Entity(tableName = "podcast_table")
data class Podcast(
    // url поста
    var url: String =     "url",

    // заголовок поста
    var title: String  =   "title",

    // дата-время поста в RFC3339
    var time: String     = "0"
) {
    @PrimaryKey(autoGenerate = true)
    var podcastId: Long = 0L

    object FromPodcastBody  {

        fun build(podcastBody: PodcastBody): Podcast {
            return Podcast(podcastBody.url, podcastBody.title, podcastBody.date.toString())
        }

    }
}

