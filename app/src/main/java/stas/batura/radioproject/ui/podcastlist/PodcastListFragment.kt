package stas.batura.radioproject.ui.podcastlist

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import stas.batura.radioproject.MainActivity
import stas.batura.radioproject.MainActivityViewModel
import stas.batura.radioproject.R
import stas.batura.radioproject.databinding.FragmentPodcastListBinding

@AndroidEntryPoint
class PodcastListFragment : Fragment() {

    private val TAG = PodcastListFragment::class.java.simpleName

    private lateinit var podcastListViewModel: PodcastListViewModel

    private lateinit var mainviewModel: MainActivityViewModel

//    private lateinit var adapter: PodcastsAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        podcastListViewModel =
                ViewModelProvider(requireActivity()).get(PodcastListViewModel::class.java)

        // TODO: проверить состояние модели после перезапуска активити
        mainviewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        val bindings: FragmentPodcastListBinding = DataBindingUtil.inflate(inflater,
        R.layout.fragment_podcast_list,
        container,
        false)
//        podcastListViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        bindings.podacstListViewModel = podcastListViewModel
        bindings.mainViewModel = mainviewModel

        bindings.lifecycleOwner = requireActivity()

        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = PodcastsAdapter(mainActivityViewModel = mainviewModel)
        podcast_recycler.adapter = adapter

//        podcastListViewModel.podcasts.observe(viewLifecycleOwner) {podcasts ->
//            adapter.submitList(podcasts)
//        }

        podcastListViewModel.currPodcasts.observe(viewLifecycleOwner) {podcasts ->
            if (podcasts != null) {
                adapter.submitList(podcasts)
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    @ExperimentalCoroutinesApi
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
    @ExperimentalCoroutinesApi
    private fun addObservers() {

        podcastListViewModel.userPref.observe(viewLifecycleOwner) {
            Log.d(TAG, "addObservers: ${it.numShownPodcasts}")
        }

    }

    /**
     * stoping observing a viewModel
     */
    private fun removeObservers() {

    }
}