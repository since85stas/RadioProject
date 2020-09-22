package stas.batura.radioproject.ui.podcastlist

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.timelable_item_view.view.*
import stas.batura.radioproject.MainActivityViewModel
import stas.batura.radioproject.data.net.TimeLabel
import stas.batura.radioproject.data.room.Podcast
import stas.batura.radioproject.databinding.TimelableItemViewBinding


class TimeStampsAdapter (
    private val mainActivityViewModel: MainActivityViewModel,
    private  val podcast: Podcast
): ListAdapter<TimeLabel, TimeStampsAdapter.ViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, mainActivityViewModel, podcast)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder (
        val binding: TimelableItemViewBinding,
        val mainActivityViewModel: MainActivityViewModel,
        val podcast: Podcast) :
        RecyclerView.ViewHolder (binding.root) {

        fun bind (timeLabel: TimeLabel) {
            binding.timeLable = timeLabel
            binding.mainviewModel = mainActivityViewModel
            binding.podcast = podcast
            binding.startTime = timeLabel.newStartTime
//            binding.mainModel = mainActivityViewModel
            binding.executePendingBindings()

            // открываем страницу с новостью
            binding.root.topic_title.setOnClickListener {
                val intent =                     Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(timeLabel.topic)
                )
                binding.root.context.startActivity(intent)
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                mainActivityViewModel: MainActivityViewModel,
                podcast: Podcast):
                    ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TimelableItemViewBinding.inflate(layoutInflater,
                    parent,
                    false)
                return ViewHolder(binding, mainActivityViewModel, podcast)
            }
        }
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<TimeLabel> (){

        override fun areItemsTheSame(
            oldItem: TimeLabel,
            newItem: TimeLabel
        ): Boolean {
            return oldItem.equals(newItem)
        }

        override fun areContentsTheSame(
            oldItem: TimeLabel,
            newItem: TimeLabel
        ): Boolean {
            return  oldItem == newItem
        }
    }



}