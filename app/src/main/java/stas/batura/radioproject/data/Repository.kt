package stas.batura.radioproject.data

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.batura.stat.batchat.repository.room.RadioDao
import stas.batura.radioproject.data.net.IRetrofit
import stas.batura.radioproject.data.room.Podcast
import javax.inject.Inject
import kotlin.random.Random

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

    @Inject
    lateinit var radioDao: RadioDao

    @Inject
    lateinit var retrofit: IRetrofit

    @ExperimentalCoroutinesApi
    val _numberFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    val numberFlow: StateFlow<Int> = _numberFlow

    init {
        Log.d(TAG, "repository started: ")
//        val res = repScope.async {
//            val res = retrofit.getPodcastByNum("223")
//
//        }
    }

    /**
     * Returns true if we should make a network request.
     */
    private suspend fun shouldUpdateRadioCacheNetw(): Boolean {
        val lastPodcast = Podcast.FromPodcastBody.build(retrofit.getLastPodcast()[0])
//        val lastPodcast = Podcast.FromPodcastBody.build(retrofit.getPodcastByNum("225"))
        val isNoInBb = radioDao.getPodcastByNum(lastPodcast.podcastId) == null
        return isNoInBb
    }

    /**
     * Returns true if we should make a network request.
     */
    @Throws(NullPointerException::class)
    private suspend fun shouldUpdateRadioCacheDB(): Boolean {
//        val lastPodcast = Podcast.FromPodcastBody.build(retrofit.getPodcastByNum("225"))
        val lastPodcast = radioDao.getLastPodcast()
        if (lastPodcast != null) {
            return lastPodcast.isWeekGone(System.currentTimeMillis())
        } else {
            return true
        }
    }

    /**
     * Update the app cache.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    override suspend fun tryUpdateRecentRadioCache() {
        if (shouldUpdateRadioCacheDB()) {
            updatePodacastInfo()
        }
    }

//    suspend fun getLastPodcast(): Podcast {
//
//    }

    /**
     * Берет информацию из последних N данных и добавляет в БД
     */
    suspend fun updatePodacastInfo() {
        val podcastBodis = retrofit.getLastNPodcasts(10)

        for (podcst in podcastBodis) {
            val podcastId = radioDao.insertPodcast(Podcast.FromPodcastBody.build(podcst))
            for (category in podcst.categories) {
//                radioDao.insertCategory(Category(podcastId, category))
            }
        }
    }

    /**
     * добавляет подкаст в базу данных
     */
    override fun addPodcast(podcast: Podcast) {
        repScope.launch {
            radioDao.insertPodcast(podcast)
        }
    }

    /**
     * выдает список подкастов из базы данных
     */
    override fun getAllPodcastsList(): Flow<List<Podcast>> {
        return radioDao.getAllPodcastsList()
    }

    override fun getlastNPodcastsList(num: Int): Flow<List<Podcast>> {
        return radioDao.getLastNPodcastsList(num)
    }

    /**
     * отмечаем что трек играет, значит он считается активным и берется по умолчанию
     */
    override fun setActivePodcast(podcastId: Int, active: Int?) {
        Log.d(TAG, "setActivePodcast out: $podcastId")
        repScope.launch {
            Log.d(TAG, "setActivePodcast: $podcastId")
//            radioDao.setAllPodIsNOTActive()
            if (active != null) {
                radioDao.setPodIsNOTActive(active)
            } else {
                radioDao.setAllPodIsNOTActive()
            }
            radioDao.setPodcastActive(podcastId)
        }
    }

    override fun getActivePodcast(): Flow<Podcast> {
        Log.d(TAG, "getActivePodcast: ")
        return radioDao.getActivePodcast().filterNotNull()
    }

    /**
     * отмечаем что трек прослушан
     */
    override fun setFinishPodcast(podcstId: Int) {
        repScope.launch {
            radioDao.setPodcastFinish(podcstId)
        }
    }

    override fun updatePodcastLastPos(podcastId: Long) {
        repScope.launch {
            radioDao.updatePodcastLastPos(podcastId)
//            radioDao.getActivePodcast()
        }
    }

    @ExperimentalCoroutinesApi
    override fun emitNumber(num: Int) {
        _numberFlow.value = (0..10).random()
        emitFlowNumber()
    }

    @ExperimentalCoroutinesApi
    override fun obsNumber(): StateFlow<Int> {
        return numberFlow
    }

    override fun emitFlowNumber(): Flow<Int> = flow<Int> {
        emit(_numberFlow.value)
    }

}