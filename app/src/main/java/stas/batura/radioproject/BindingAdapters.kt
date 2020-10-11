package stas.batura.radioproject

import android.util.Log
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

@BindingAdapter(value = ["playProgressBarVisibility","podcastIsVisible"])
fun ProgressBar.bindplayPVisibility(visible: Boolean, podcast: Podcast) {
//        if (visible) {
//            visibility = View.VISIBLE
//        } else {
//            visibility = View.GONE
//        }
    val active = podcast.isActive
    if (active) {
        Log.d("bindplayPVisibility", "$active.isActive is active: ")
        if (visible) {
            Log.d("bindplayPVisibility", "$visible is visible: ")
            visibility = View.VISIBLE
        } else {
            Log.d("bindplayPVisibility", "$visible not visible: ")
            visibility = View.GONE
        }
    } else {
        visibility = View.GONE
        Log.d("bindplayPVisibility", "$active not active: ")
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
    Log.d("controlTitle", "controlTitleBind: $podcast")
    text = podcast?.title
}