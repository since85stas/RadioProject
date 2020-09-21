package stas.batura.radioproject.ui.podcastlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import stas.batura.radioproject.MainActivityViewModel
import stas.batura.radioproject.data.net.TimeLabel
import stas.batura.radioproject.databinding.TimelableItemViewBinding

class TimeStampsAdapter (val mainActivityViewModel: MainActivityViewModel):
    ListAdapter<TimeLabel, TimeStampsAdapter.ViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, mainActivityViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder (val binding: TimelableItemViewBinding, val mainActivityViewModel: MainActivityViewModel) :
        RecyclerView.ViewHolder (binding.root) {

        fun bind (timeLabel: TimeLabel) {
            binding.timeLable = timeLabel
//            binding.mainModel = mainActivityViewModel
            binding.executePendingBindings()
        }

        fun onItemClicked () {

        }

        companion object {
            fun from(parent: ViewGroup, mainActivityViewModel: MainActivityViewModel): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TimelableItemViewBinding.inflate(layoutInflater,
                    parent,
                    false)
                return ViewHolder(binding, mainActivityViewModel)
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