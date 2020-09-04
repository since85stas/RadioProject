package stas.batura.radioproject.ui.podcastlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_podcast_list.*
import stas.batura.radioproject.R
import stas.batura.radioproject.databinding.FragmentPodcastListBinding

@AndroidEntryPoint
class PodcastListFragment : Fragment() {

    private val TAG = PodcastListFragment::class.java.simpleName

    private lateinit var podcastListViewModel: PodcastListViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        podcastListViewModel =
                ViewModelProvider(this).get(PodcastListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_podcast_list, container, false)
        val textView: TextView = root.findViewById(R.id.text_poscast)

        val bindings: FragmentPodcastListBinding = DataBindingUtil.inflate(inflater,
        R.layout.fragment_podcast_list,
        container,
        false)
//        podcastListViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        bindings.podacstListViewModel = podcastListViewModel


        bindings.lifecycleOwner = viewLifecycleOwner



        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = PodcastsAdapter()
        podcast_recycler.adapter = adapter

        podcastListViewModel.podcasts.observe(viewLifecycleOwner) {podcasts ->
            adapter.submitList(podcasts)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        addObservers()
        super.onStart()
    }

    override fun onPause() {
        removeObservers()
        super.onPause()
    }

    /**
     * starting observing a viewModel when fragment is active
     */
    private fun addObservers() {

    }

    /**
     * stoping observing a viewModel
     */
    private fun removeObservers() {

    }
}