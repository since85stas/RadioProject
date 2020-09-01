package stas.batura.radioproject.data

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import ru.batura.stat.batchat.repository.room.RadioDao
import stas.batura.radioproject.data.net.StatusResponse
import stas.batura.radioproject.data.room.Podcast
import javax.inject.Inject

class Repository () {

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

//    override fun getSongText(title: String): Deferred<StatusResponse> {
//        Log.d(TAG, "test")
//        return
//    }

    fun addPodcast(podcast: Podcast): Long {
        return repository.insertPodcast(podcast)
    }

    fun getPodcastsList(): Flow<List<Podcast>> {
        return repository.getPodcastsList()
    }


}