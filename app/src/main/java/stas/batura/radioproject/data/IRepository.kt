package stas.batura.radioproject.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.batura.stat.batchat.repository.room.RadioDao
import stas.batura.radioproject.UserPreferences
import stas.batura.radioproject.data.dataUtils.Year
import stas.batura.radioproject.data.net.IRetrofit
import stas.batura.radioproject.data.room.Podcast

interface IRepository {

    suspend fun tryUpdateRecentRadioCache()

    fun addPodcast(podcast: Podcast)

//    fun getAllPodcastsList(): Flow<List<Podcast>>

//    fun getlastNPodcastsList(num: Int): Flow<List<Podcast>>

    fun setActivePodcast(podcastId: Int, active: Int?)

    fun getActivePodcast(): Flow<Podcast>

    suspend fun getActivePodcastSus(podcastId: Int): Podcast?

    fun setFinishPodcast(podcstId: Int)

    fun updatePodcastLastPos(podcastId: Int, position: Long)

    fun getUserPrefPNumber(): Flow<Int>

    fun getUserPrefSmallVis(): Flow<Boolean>

    fun setNumPodcsts(num: Int)

    fun setPrefPodcastIsSmall(bol: Boolean)

    fun getPrefActivePodcastNum(): Flow<Int>

    fun getPrefActivePodcast(): Flow<Podcast>

    fun setPrefActivePodcastNum(num: Int)

    fun setPrefListType(type:ListViewType)

    fun getPrefListType(): Flow<ListViewType>

    @ExperimentalCoroutinesApi
    fun currentPodcList(): StateFlow<List<Podcast>?>

    suspend fun getAllPodcastListFlow()

    suspend fun getLastNPodcastListState(num: Int)

    fun getLastNPodcastListFlow(num: Int): Flow<List<Podcast>>

    suspend fun getPodcastByYearState(year: Year)

    fun getPodcastByYearFlow(year: Year): Flow<List<Podcast>>
}

enum class ListViewType(type: Int) {

    NUMBER(0),
    YEAR(1),
    MONTH(2);

    companion object {
        private val VALUES = values()

        fun getByValue(value: Int) = VALUES.firstOrNull { it.ordinal == value }
    }

}