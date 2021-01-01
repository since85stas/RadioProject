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

@ExperimentalCoroutinesApi
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
//        val lastT = getPrefLastPtime()
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

    fun getLastNPodcastListFlow(num: Int): Flow<List<Podcast>> {
        return radioDao.getLastNPodcastsList(num)
    }

    fun getNPodcastsListHighFromCurrent(num: Int, time: Long): Flow<List<Podcast>> {
        Log.d(TAG, "getNPodcastsListHighFromCurrent: $time")
        val flowList = radioDao.getNPodcastsListHighFromCurrent(num, time)
        repScope.launch {
            val lastT = flowList.firstOrNull()
            if (lastT!=null && lastT.size>0) {
//                setPrefLastPtime(lastT.first().timeMillis)
            }
        }
        return flowList
    }

    fun getNPodcastsListLowFromCurrent(num: Int, time: Long): Flow<List<Podcast>> {
        Log.d(TAG, "getNPodcastsListLowFromCurrent: $time $num")
        val flowList = radioDao.getNPodcastsListLowFromCurrent(num, time)
        repScope.launch {
            val lastT = flowList.firstOrNull()
            if (lastT!=null && lastT.size>0) {
//                setPrefLastPtime(lastT.last().timeMillis)
//                setPrefFirstPtime(lastT.first().timeMillis)
            }
        }
        return flowList
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
     * получаем число отображаемых подкастов
     */
    fun getUserPrefPNumber(): Flow<Int> {
        return protoData.data.map { it ->
            it.numShownPodcasts
        }
    }

//    override fun getUserPrefSmallVis(): Flow<Boolean> {
//        return protoData.data.map {
//            it.podcastIsSmall
//        }
//    }

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

//    /**
//     * устанавливаем показывать полный или сокращенный подкаст
//     */
//    override fun setPrefPodcastIsSmall(bol: Boolean) {
//        repScope.launch {
//            protoData.updateData { t: UserPreferences ->
//                t.toBuilder().setPodcastIsSmall(bol).build()
//            }
//        }
//    }

    /**
     * устанавливаем по какому типу отображать подкасты
     */
    override fun setPrefListType(type: ListViewType) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setListViewType(type.ordinal).build()
            }
        }
    }

    /**
     * получаем по какому типу отображать подкасты
     */
    override fun getPrefListType(): Flow<ListViewType> {
        return protoData.data.map {
            ListViewType.getByValue(it.listViewType)!!
        }
    }

    /**
     * получаем список подкастов за выбранный год из БД
     */
    private fun getPodcastByYearFlow(year: Year): Flow<List<Podcast>> {
        return radioDao.getPodcastsBetweenTimes(year.yearS, year.yearE)
    }

    /**
     * получаем номер активный подкаст
     */
    override fun getPrefActivePodcastNum(): Flow<Int> {
        return protoData.data.map {
            it.activePodcNum
        }
    }

    /**
     * записывем номер активный подкаст
     */
    override fun setPrefActivePodcastNum(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setActivePodcNum(num).build()
            }
        }
    }

    override fun setPrefNumOnPage(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setNumberOnPage(num).build()
            }
        }
    }

    /**
     * записываем выбранный для отображения год
     */
    override fun setPrefSelectedYear(year: Year) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setSelectedYear(year.ordinal).build()
            }
        }
    }

    /**
     * получаем выбранный для отображения год
     */
    fun getPrefSelectedYear(): Flow<Year> {
        return protoData.data.map {
            Year.getByValue(it.selectedYear)!!
        }
    }

    /**
     * получаем активный подкаст
     */
    override fun getPrefActivePodcast(): Flow<Podcast> {
        val num = getPrefActivePodcastNum()
        return flow<Podcast> {
            radioDao.getPodcastByNum(num.first())
        }
    }

    override suspend fun getActivePodcastSus(podcastId: Int): Podcast? {
        return radioDao.getPodcastByNum(podcastId)
    }

    /**
     * список по порядку
     */
    override fun numberTypeList(lastId:Int): Flow<List<Podcast>> {
        return getUserPrefPNumber().flatMapLatest {
                num -> getNPodcastsListLowFromCurrent(num, time)
        }
    }

    /**
     * список за год
     */
    override fun yearTypeList(): Flow<List<Podcast>> {
        return getPrefSelectedYear().flatMapLatest { year ->
            getPodcastByYearFlow(year)
        }
    }

    override fun updateTrackDuration(podcastId: Int, duration: Long) {
        repScope.launch {
            radioDao.updateTrackDuration(podcastId, duration)
        }
    }

    override fun updateTrackIdDetailed(podcastId: Int, isDetailed: Boolean) {
        repScope.launch {
            radioDao.updateTrackIdDetailed(podcastId, isDetailed)
        }
    }

    /**
     * записываем выбранный для отображения год
     */
    override fun setPrefLastPnumb(numb: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setLastPodcNumb(numb).build()
            }
        }
    }

    /**
     * получаем выбранный для отображения год
     */
    override fun getPrefLastPnumb(): Flow<Int> {
        return protoData.data.map {
            it.lastPodcNumb
        }
    }

    override fun setPrefFirstPnumb(numb: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setFirstPodcNumb(numb).build()
            }
        }
    }

    /**
     * получаем выбранный для отображения год
     */
    override fun getPrefFirstPnumb(): Flow<Int> {
        return protoData.data.map {
            it.firstPodcNumb
        }
    }

//    override suspend fun PrefLastPtime(): Long? {
//        return getPrefLastPtime().firstOrNull()
//    }

    override fun getTypeAndNumb(): Flow<PodcastLoadInfo> =
        getPrefListType().combine(getPrefLastPnumb()) {num, time ->
            PodcastLoadInfo(num, time)
    }
}

data class PodcastLoadInfo(
    val listType: ListViewType,
    val lastNumb: Int
)