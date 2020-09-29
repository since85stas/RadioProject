package stas.batura.radioproject

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import kotlinx.android.synthetic.main.control_fragment_new.view.*
import stas.batura.radioproject.data.net.TimeLabel
import stas.batura.radioproject.data.room.Podcast
import java.text.SimpleDateFormat

@BindingAdapter("titleBind")
fun TextView.podcastTitleBind(podcast: Podcast) {
    text = podcast.title
}

@BindingAdapter("urlBind")
fun TextView.podcastUrlBind(podcast: Podcast) {
    text = podcast.url
}

@BindingAdapter("progressBarVisibility")
fun ProgressBar.bindVisibility(visible: Boolean) {
    if (visible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@BindingAdapter("timelableTimeBind")
fun TextView.timelableTimeBind(timeLabel: TimeLabel) {
    text = timeLabel.newStartTime.toString()
}

@BindingAdapter("timelableTitleBind")
fun TextView.timelableTitleBind(timeLabel: TimeLabel) {
    text = timeLabel.topic
}

@BindingAdapter("bindExoPla")
fun PlayerControlView.bindPlayer(exoPlayer: ExoPlayer?) {
    if (exoPlayer != null) {
        player = exoPlayer
    }
}

@BindingAdapter("controlTitleBind")
fun TextView.controlTitleBind(podcast: Podcast?) {
    text = podcast?.title
}