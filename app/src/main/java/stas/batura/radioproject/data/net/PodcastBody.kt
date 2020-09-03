package stas.batura.radioproject.data.net

data class PodcastBody (
    var url: String  ,  // url поста
    var title: String,  // заголовок поста
    var date:  String,  // дата-время поста в RFC3339
    var categories: List<String>, // список категорий, массив строк
    var imageUrl: String,    // url картинки
    var fileName:   String,  // имя файла
    var bodyHtml: String,     // тело поста в HTML
    var postText: String,  // пост в текстовом виде
    var audioUrl: String,   // url аудио файла
    var timeLables: List<Long>) // массив временых меток тем

data class TimeLabel (
    val topic: String, // название темы
    val startTime: String,    // время начала в RFC3339
    val duration: Int    // длительность в секундах
)
