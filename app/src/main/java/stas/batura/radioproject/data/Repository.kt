package stas.batura.radioproject.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import ru.batura.stat.batchat.repository.room.RadioDao
import stas.batura.radioproject.data.net.IRetrofit
import stas.batura.radioproject.data.net.StatusResponse
import stas.batura.radioproject.data.room.Podcast
import javax.inject.Inject

class Repository @Inject constructor(): IRepository {

    private val TAG = Repository::class.java.simpleName

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var repositoryJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [repositoryJob], any coroutine started in this uiScope can be cancelled
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android.
     */
    private val repScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    @Inject lateinit var repository: RadioDao

    @Inject lateinit var retrofit: IRetrofit

    init {
        Log.d(TAG, "repository started: ")
        val res = repScope.async {
            val res = retrofit.getPodcastByNum("223")

        }
    }

    override fun addPodcast(podcast: Podcast){
        repScope.launch {
            repository.insertPodcast(podcast)
        }
    }

    override fun getPodcastsList(): Flow<List<Podcast>> {
        return repository.getPodcastsList()
    }


}