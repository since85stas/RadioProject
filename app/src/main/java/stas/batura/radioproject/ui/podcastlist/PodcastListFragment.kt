package stas.batura.radioproject.ui.podcastlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_podcast_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import stas.batura.radioproject.MainActivityViewModel
import stas.batura.radioproject.R
import stas.batura.radioproject.databinding.FragmentPodcastListBinding

@AndroidEntryPoint
class PodcastListFragment : Fragment() {

    private val TAG = PodcastListFragment::class.java.simpleName

    private lateinit var podcastListViewModel: PodcastListViewModel

    private lateinit var mainviewModel: MainActivityViewModel

    private lateinit var adapter: PodcastsAdapter

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

        bindings.lifecycleOwner = viewLifecycleOwner

        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PodcastsAdapter(mainActivityViewModel = mainviewModel, listModel = podcastListViewModel)
        podcast_recycler.adapter = adapter

//        podcastListViewModel.podcasts.observe(viewLifecycleOwner) {podcasts ->
//            adapter.submitList(podcasts)
//        }

        podcastListViewModel.currPodcasts.observe(viewLifecycleOwner) {podcasts ->
            if (podcasts != null) {
                adapter.submitList(podcasts)
                Log.d(TAG, "onViewCreated: size ${podcasts.size}")
            } else {
                Log.d(TAG, "onViewCreated: podcasts is null")
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

        podcastListViewModel.userPrefNum.observe(viewLifecycleOwner) {
            podcastListViewModel.setNumberPodcasts(it)
        }

        podcastListViewModel.userPrefSmallV.observe(viewLifecycleOwner) {
            Log.d(TAG, "addObservers: visible $it")
            if (it) {
                mainviewModel.setCheckBoxInitState(it)
            }
            adapter.notifyDataSetChanged()
        }

        podcastListViewModel.activeNumPref.observe(viewLifecycleOwner) {
            Log.d(TAG, "activeNumberPref: $it")
        }

    }

    /**
     * stoping observing a viewModel
     */
    private fun removeObservers() {

    }
}