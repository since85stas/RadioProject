package stas.batura.radioproject.ui.podcastlist

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import stas.batura.radioproject.data.IRepository
import stas.batura.radioproject.data.room.Podcast

class PodcastListViewModel @ViewModelInject constructor(val repository: IRepository): ViewModel() {

    private val TAG = PodcastListViewModel::class.java.simpleName

    @ExperimentalCoroutinesApi
    val numberLive = repository.obsNumber()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _spinner = MutableLiveData<Boolean>(false)
    /**
     * Show a loading spinner if true
     */
    val spinner: LiveData<Boolean>
        get() = _spinner

    val text: LiveData<String> = _text

//    val podcasts: LiveData<List<Podcast>> = repository.getAllPodcastsList().asLiveData()
    val podcasts: LiveData<List<Podcast>> = repository.getlastNPodcastsList(5).asLiveData()

    val podcastsFlow: Flow<List<Podcast>> = repository.getlastNPodcastsList(5)

    val currPodcasts = repository.currentPodcList().asLiveData()

    val combineFlow = combine(
        numberLive,
        podcastsFlow
    ) {
        number: Int, podc: List<Podcast> -> return@combine " combine: $number, $podc "
    }.asLiveData()

    val flowNumberLive = repository.obsNumber().asLiveData()

    val userPref = repository.getUserPref().asLiveData()

    init {
        launchDataLoad {
            repository.tryUpdateRecentRadioCache()
        }
//        repository.
    }

    fun addPodcast() {
        launchDataLoad {
            repository.tryUpdateRecentRadioCache()
        }
    }

    /**
     * Helper function to call a data load function with a loading spinner; errors will trigger a
     * snackbar.
     *
     * By marking [block] as [suspend] this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the viewModelScope. Before calling
     *              the lambda, the loading spinner will display. After completion or error, the
     *              loading spinner will stop.
     */
    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                Log.d(TAG, "launchDataLoad: " + error)
//                _snackbar.value = error.message
            } finally {
                _spinner.value = false

                repository.getAllPodcastListFlow()
            }
        }
    }

    fun setNumberPodcasts(number: Int) {
       val num = (0..10).random()

        viewModelScope.launch {
            repository.getLastNPodcastListFlow(num)
        }
    }
}