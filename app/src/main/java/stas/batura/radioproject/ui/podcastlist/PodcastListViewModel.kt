package stas.batura.radioproject.ui.podcastlist

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import stas.batura.radioproject.data.IRepository
import stas.batura.radioproject.data.ListViewType
import stas.batura.radioproject.data.room.Podcast

@ExperimentalCoroutinesApi
class PodcastListViewModel @ViewModelInject constructor(val repository: IRepository): ViewModel() {

    private val TAG = PodcastListViewModel::class.java.simpleName

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

    val activeNumPref = repository.getPrefActivePodcastNum().asLiveData()

    // получаем список в зависимости от типа отображения
    val newPodcastList: LiveData<List<Podcast>> = repository.getTypeAndNumb().
        flatMapLatest { loadInfo ->
            if (loadInfo.listType == ListViewType.YEAR) {
                repository.yearTypeList()
            } else {
                repository.numberTypeList(loadInfo.lastNumb)
            }
        }.asLiveData()

    init {
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
                repository.updateLastPodcPrefsNumber()
//                repository.getAllPodcastListFlow()
            }
        }
    }
    
    fun onDetailCheckClick(boolean: Boolean) {
        Log.d(TAG, "onDetailCheckClick: $boolean")
    }

    /**
     * указываем выводить ли детали для текущеего подкаста
     */
    fun onEnabled(podcast: Podcast, enabled: Boolean) {
        Log.d(TAG, "onEnabled: ")
        repository.updateTrackIdDetailed(podcast.podcastId, enabled)
    }

    fun getNextNPodcasts() {
//        repository.setPrefLastPtime(pod)
    }

    /**
     * получаем следующие num объектов в список
     */
    fun changeNextListByNum(num: Int) {
        viewModelScope.launch {
            repository.changeLastPnumberByValue(num)
        }
    }
}