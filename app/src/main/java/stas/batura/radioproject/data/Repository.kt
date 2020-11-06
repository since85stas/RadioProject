package stas.batura.radioproject.data

import android.util.Log
import androidx.datastore.DataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.batura.stat.batchat.repository.room.RadioDao
import stas.batura.radioproject.UserPreferences
import stas.batura.radioproject.data.dataUtils.Year
import stas.batura.radioproject.data.net.IRetrofit
import stas.batura.radioproject.data.room.Podcast
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class Repository @Inject constructor() : IRepository {

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

    @Inject
    lateinit var protoData: DataStore<UserPreferences>

    // контейнер для передачи массива в UI
    val _currentPodcList: MutableStateFlow<List<Podcast>?> = MutableStateFlow(null)
    val currentPodcList: StateFlow<List<Podcast>?> = _currentPodcList

    init {
        Log.d(TAG, "repository started: ")
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
        val podcastBodis = retrofit.getLastNPodcasts(100)

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
//    override fun getAllPodcastsList(): Flow<List<Podcast>> {
//        return radioDao.getAllPodcastsList()
//    }

//    override fun getlastNPodcastsList(num: Int): Flow<List<Podcast>> {
//        return radioDao.getLastNPodcastsList(num)
//    }

    /**
     * запускается при первом запуске, возможно моно будет убрать
     */
    override suspend fun getAllPodcastListFlow() {
//        repScope.launch {
        Log.d(TAG, "getAllPodcastListFlow: ")
        val flow = radioDao.getLastNPodcastsList(100)
        flow.collect() {
            _currentPodcList.value = it
        }
//        }
    }

    /**
     * получаем последние N подкастов
     */
    override suspend fun getLastNPodcastListFlow(num: Int) {
        Log.d(TAG, "getLastNPodcastListFlow: $num")
//        repScope.launch {
        val flow = radioDao.getLastNPodcastsList(num)
        flow.collect() {
            _currentPodcList.value = it
        }
//        }
    }

    /**
     * отмечаем что трек играет, значит он считается активным и берется по умолчанию
     */
    override fun  setActivePodcast(podcastId: Int, active: Int?) {
        repScope.launch {
            Log.d(TAG, "setActivePodcast: $podcastId")
//            radioDao.setAllPodIsNOTActive()
            if (active != null) {
                radioDao.setPodIsNOTActive(active)
            } else {
                Log.d(TAG, "setActivePodcast: All")
                radioDao.setAllPodIsNOTActive()
            }
            radioDao.setPodcastActive(podcastId)
        }
    }

    /**
     * получаем активный подкаст
     */
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

    /**
     * обновляем информацию на каком месте закончили проигрывать трек
     */
    override fun updatePodcastLastPos(podcastId: Int, position: Long) {
        repScope.launch {
            radioDao.updatePodcastLastPos(podcastId, position)
        }
    }

    /***
     * получаем файл с настройками возможно надо будет разбить на отдельные запросы
     */
    override fun getUserPrefPNumber(): Flow<Int> {
        return protoData.data.map { it ->
            it.numShownPodcasts
        }
    }

    override fun getUserPrefSmallVis(): Flow<Boolean> {
        return protoData.data.map {
            it.podcastIsSmall
        }
    }

    /**
     * записываем число показоваемых треков в настройки
     */
    override fun setNumPodcsts(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setNumShownPodcasts(num).build()
            }
        }
    }

    override fun setPrefPodcastIsSmall(bol: Boolean) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setPodcastIsSmall(bol).build()
            }
        }
    }

    override fun setPrefListType(type: ListViewType) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setListViewType(type.ordinal).build()
            }
        }
    }

    override fun getPrefListType(): Flow<ListViewType> {
        return protoData.data.map {
            ListViewType.getByValue(it.listViewType)!!
        }
    }

    /**
     * передаем текущие подкасты в UI
     */
    @ExperimentalCoroutinesApi
    override fun currentPodcList(): StateFlow<List<Podcast>?> {
        Log.d(TAG, "currentPodcList: ")
        return currentPodcList
    }

    override suspend fun getPodcastByYear(year: Year) {
        Log.d(TAG, "getPodcastByYear: ${year.yearS} ${year.yearE}")
        val flow = radioDao.getPodcastsBetweenTimes(year.yearS, year.yearE)
        flow.collect() {
            _currentPodcList.value = it
        }
    }

    override fun getPrefActivePodcastNum(): Flow<Int> {
        return protoData.data.map {
            it.activePodcNum
        }
    }

    override fun setPrefActivePodcastNum(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setActivePodcNum(num).build()
            }
        }
    }

    override fun getPrefActivePodcast(): Flow<Podcast> {
        val num = getPrefActivePodcastNum()
        return flow<Podcast> {
            radioDao.getPodcastByNum(num.first())
        }
    }

    override suspend fun getActivePodcastSus(podcastId: Int): Podcast? {
        return radioDao.getPodcastByNum(podcastId)
    }
}