package stas.batura.radioproject.data.net

import com.squareup.moshi.Json

data class PodcastBody (
    var url: String  ,  // url поста
    var title: String,  // заголовок поста
    var date:  String,  // дата-время поста в RFC3339
    var categories: List<String>, // список категорий, массив строк

    @Json(name = "image")
    var imageUrl: String,    // url картинки

    @Json(name = "file_name")
    var fileName:   String,  // имя файла

    @Json(name = "body")
    var bodyHtml: String,     // тело поста в HTML

    @Json(name = "show_notes")
    var postText: String,  // пост в текстовом виде

    @Json(name = "audio_url")
    var audioUrl: String,   // url аудио файла

    @Json(name = "time_labels")
    var timeLables: List<TimeLabel>? = null) // массив временых меток тем

data class TimeLabel (
    val topic: String, // название темы

    @Json(name = "time")
    val startTime: String,    // время начала в RFC3339
    val duration: Int    // длительность в секундах
)
