package stas.batura.radioproject

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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

@BindingAdapter("recyclerBarVisibility")
fun RecyclerView.bindVisibility(visible: Boolean) {
    if (visible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@BindingAdapter(value = ["playProgressBarVisibility","currentPodcast", "activePodcast"])
fun ProgressBar.bindplayPVisibility(visible: Boolean, podcast: Podcast, podcastActive: Podcast?) {
    if (visible) {
//        Log.d("bindplayPVisibility", "$visible is visible: ")
        if (podcastActive != null) {
            if (podcast == podcastActive) {
//                Log.d("bindplayPVisibility", "is active: ")
                visibility = View.VISIBLE
            } else {
//                Log.d("bindplayPVisibility", "not active: ")
                visibility = View.GONE
            }
        }
    } else {
        visibility = View.GONE
        Log.d("bindplayPVisibility", "$visible not visible: ")
    }
}

@BindingAdapter("timelableTimeBind")
fun TextView.timelableTimeBind(timeLabel: TimeLabel) {
    if (timeLabel.startTimeString!= null) {
        text = timeLabel.startTimeString
    }
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

@BindingAdapter("combineTitleBind")
fun TextView.combineTitleBind(string: String?) {
    Log.d("controlTitle", "combineTitleBind: $string")
    if (string != null) {
        text = string
    }
}

@BindingAdapter("bindProgress")
fun ProgressBar.bindProgress(int: Int) {
    Log.d("bind progress", "bindProgress: $int" )
    progress = int
}

//@BindingAdapter("bindSmallCheckbox")
//fun CheckBox.bindSmallCheckbox(model: MainActivityViewModel) {
//    model.setPodcastIsSmall(isChecked)
//}

