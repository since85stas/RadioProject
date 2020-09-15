package stas.batura.radioproject.ui.podcastlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import stas.batura.radioproject.MainActivityViewModel
import stas.batura.radioproject.data.room.Podcast
import stas.batura.radioproject.databinding.PodcastItemViewBinding

class PodcastsAdapter (val mainActivityViewModel: MainActivityViewModel):
    ListAdapter<Podcast, PodcastsAdapter.ViewHolder>(TrackDiffCalback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, mainActivityViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder (val binding: PodcastItemViewBinding, val mainActivityViewModel: MainActivityViewModel ) :
        RecyclerView.ViewHolder (binding.root) {

        fun bind (podcast: Podcast) {
            binding.podcast = podcast
            binding.mainModel = mainActivityViewModel
            binding.executePendingBindings()
        }

        fun onItemClicked () {

        }

        companion object {
            fun from(parent: ViewGroup, mainActivityViewModel: MainActivityViewModel): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PodcastItemViewBinding.inflate(layoutInflater,
                    parent,
                    false)
                return ViewHolder(binding, mainActivityViewModel)
            }
        }
    }

    class TrackDiffCalback : DiffUtil.ItemCallback<Podcast> (){

        override fun areItemsTheSame(
            oldItem: Podcast,
            newItem: Podcast
        ): Boolean {
            return oldItem.equals(newItem)
        }

        override fun areContentsTheSame(
            oldItem: Podcast,
            newItem: Podcast
        ): Boolean {
            return  oldItem == newItem
        }
    }



}