package stas.batura.radioproject.ui.podcastlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import stas.batura.radioproject.R

class PodcastListFragment : Fragment() {

    private lateinit var podcastListViewModel: PodcastListViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        podcastListViewModel =
                ViewModelProviders.of(this).get(PodcastListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_podcast_list, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        podcastListViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}