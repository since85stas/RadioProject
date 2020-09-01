package stas.batura.radioproject.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "podcast_table")
data class Podcast(
    // url поста
    var url: String =     "url",

    // заголовок поста
    var title: String  =   "title",

    // дата-время поста в RFC3339
    var time: Long     = 0L
) {
    @PrimaryKey(autoGenerate = true)
    var podcastId: Long = 0L
}

