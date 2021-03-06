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
import stas.batura.radioproject.data.room.SavedStatus
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
        if (isFirstOpen().first()) {
            if (shouldUpdateRadioCacheDB()) {
                updatePodacastAllInfo()
            }
            setFistOpen(true)
        } else {
            if (shouldUpdateRadioCacheDB()) {
                val lastPodcast = radioDao.getLastPodcast()
                lastPodcast?.let {
                    updatePodacastLastNumInfo(it.numWeekGone(System.currentTimeMillis()))
                }

            }
        }
    }

    /**
     * Берет информацию из последних N данных и добавляет в БД
     */
    suspend fun updatePodacastAllInfo() {
        val podcastBodis = retrofit.getLastNPodcasts(200)
        for (podcst in podcastBodis) {
            val podcastId = radioDao.insertPodcast(Podcast.FromPodcastBody.build(podcst))
        }
    }

    /**
     * Берет информацию из последних N данных и добавляет в БД
     * @param num число подкастов которое будем запрашивать
     */
    suspend fun updatePodacastLastNumInfo(num: Int) {

        // делаем запрос на {num} последних записей с сервера
        val podcastBodis = retrofit.getLastNPodcasts(num)

        // сохраняем полученной в БД
        for (podcst in podcastBodis) {
            val podcastId = radioDao.insertPodcast(Podcast.FromPodcastBody.build(podcst))
        }
    }

    /**
     * устанавливаем кокой номер подкаста последний на данный момент в БД
     */
    override fun updateLastPodcPrefsNumber() {
        repScope.launch {
            val lastPodcast = radioDao.getLastPodcast()
            lastPodcast?.let {
                Log.d(TAG, "updatePodacastInfo: $lastPodcast")
                setPrefLastPnumb(it.podcastId)
                setPrefMaxPnumb(it.podcastId)
            }
        }
    }

    /**
     * добавляет подкаст в базу данных
     * @param podcast сохраняемый подкаст
     */
    override fun addPodcast(podcast: Podcast) {
        repScope.launch {
            radioDao.insertPodcast(podcast)
        }
    }

    /**
     * берем N подкастов с номером меньше заданного
     * @param podcId номер от которого остчитываем
     * @param num число подкастов
     *
     * В итоге выводится список с номерами podcId до (podcId-num)
     */
    fun getNPodcastsListBeforeId(num: Int, podcId: Int): Flow<List<Podcast>> {
        Log.d(TAG, "getNPodcastsListBeforeId: $podcId $num")
        val flowList = radioDao.getNPodcastsListBeforeId(num, podcId)
        return flowList
    }

//    /**
//     * отмечаем что трек прослушан
//     */
//    override fun setFinishPodcast(podcstId: Int) {
//        repScope.launch {
//            radioDao.setPodcastFinish(podcstId)
//        }
//    }

    /**
     * обновляем информацию на каком месте закончили проигрывать трек
     * @param podcastId номер подкаста
     * @param время в мс
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

    /**
     * устанавливаем по какому типу отображать подкасты
     * @param type тип выводимого списка
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
    fun getPrefListType(): Flow<ListViewType> {
        return protoData.data.map {
            ListViewType.getByValue(it.listViewType)!!
        }
    }

    /**
     * получаем список подкастов за выбранный год из БД
     * @param year год за который выводим список
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
     * отмечаем что трек играет, значит он считается активным и берется по умолчанию
     * @param num номер активного подкаста
     */
    override fun setPrefActivePodcastNum(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setActivePodcNum(num).build()
            }
        }
    }

    /**
     * устанавливаем число отображаемых подкастов на странице
     * @param num число выводимых подкастов
     */
    override fun setPrefNumOnPage(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setNumberOnPage(num).build()
            }
        }
    }

    /**
     * записываем выбранный для отображения год
     * @param year год
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

    /**
     * получить активный подкаст по номеру
     * @param podcastId номер подкаста
     */
    override suspend fun getActivePodcastSus(podcastId: Int): Podcast? {
        return radioDao.getPodcastByNum(podcastId)
    }

    /**
     * список по порядку
     * @param lastId номер последнего подкаста от которого выводим номера
     */
    override fun numberTypeList(lastId:Int): Flow<List<Podcast>> {
        return getUserPrefPNumber().flatMapLatest {
                num -> getNPodcastsListBeforeId(num, lastId)
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

    /**
     * список за год
     */
    override fun favTypeList(): Flow<List<Podcast>> {
        return radioDao.getFavoritesPodcastsList()
    }

    /**
     * задаем длительность подкаста в милисекндах
     * @param podcastId номер подкаста
     * @param duration длительность в мс
     */
    override fun updateTrackDuration(podcastId: Int, duration: Long) {
        repScope.launch {
            radioDao.updateTrackDuration(podcastId, duration)
        }
    }

    /**
     * задаем выводить ли доп инф о подкасте
     * @param podcastId номер подкаста
     * @param isDetailed статус вывода
     */
    override fun updateTrackIdDetailed(podcastId: Int, isDetailed: Boolean) {
        repScope.launch {
            radioDao.updateTrackIdDetailed(podcastId, isDetailed)
        }
    }

    /**
     * сохраняем номер последнего подкаста из отображаемых на экране
     * @param numb номер подкаста
     */
    fun setPrefLastPnumb(numb: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setLastPodcNumb(numb).build()
            }
        }
    }

    /**
     * получаем выбранный для отображения год
     */
    fun getPrefLastPnumb(): Flow<Int> {
        return protoData.data.map {
            it.lastPodcNumb
        }
    }

    /**
     * сохраняем номер последнего подкаста в БД
     * @param numb номер подкаста
     */
    fun setPrefMaxPnumb(numb: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setMaxPodcNumb(numb).build()
            }
        }
    }

    /**
     * получаем выбранный для отображения год
     */
    fun getPrefMaxPnumb(): Flow<Int> {
        return protoData.data.map {
            it.maxPodcNumb
        }
    }

    override fun getTypeAndNumb(): Flow<PodcastLoadInfo> =
        getPrefListType().combine(getPrefLastPnumb()) {num, time ->
            PodcastLoadInfo(num, time)
    }

    /**
     * сохраняем и переходим на след диаппозон показываемых подкастов
     * @param num - метка если равна
     *             1: то переходим на диапозон выше
     *             -1: то переходим на диапозон ниже
     */
    override suspend fun changeLastPnumberByValue(num: Int) {
        val lastId = getPrefLastPnumb().first()
        val maxId = getPrefMaxPnumb().first()
        val numb = getUserPrefPNumber().first()
        if (num == 1) {
            if (lastId + numb < maxId) setPrefLastPnumb(lastId + numb) else setPrefLastPnumb(maxId)
        } else if (num == -1){
            if (lastId - numb > 0) setPrefLastPnumb(lastId -numb) else setPrefLastPnumb(numb)
        }
    }

    override suspend fun updateRedrawField(podcastId: Int) {
        radioDao.updateRedrawField(podcastId)
    }

    /**
     * проверяем первый ли это запуск
     */
    fun isFirstOpen(): Flow<Boolean> {
        return protoData.data.map {
            !it.isNotFirstOpen
        }
    }

    /**
     * устанавливаем после первого открытия программы
     * @param boolean если True - значит уже запустили
     */
    suspend fun setFistOpen(boolean: Boolean) {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setIsNotFirstOpen(boolean).build()
            }
    }

    /**
     * отмечаем подкаст в спимок избранных или из него
     * @param podcastId номер подкаста
     * @param status в избранное или нет
     */
    override fun setFavoriteStatus(podcastId: Int, status: Boolean) {
        repScope.launch {
            radioDao.setPodFavoriteStatus(podcastId, status)
        }
    }

    /**
     * отмечаем сохранен подкаст или нет
     * @param podcastId номер подкаста
     * @param status статус сохранения подкаста
     */
    override fun updatePodcastSavedStatus(podcastId: Int, savedStatus: SavedStatus) {
        repScope.launch {
            radioDao.updatePodcastSavedStatus(podcastId, savedStatus)
        }
    }

}

data class PodcastLoadInfo(
    val listType: ListViewType,
    val lastNumb: Int
)