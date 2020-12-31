package stas.batura.radioproject.data

import kotlinx.coroutines.flow.Flow
import stas.batura.radioproject.data.dataUtils.Year
import stas.batura.radioproject.data.room.Podcast

interface IRepository {

    suspend fun tryUpdateRecentRadioCache()

    fun addPodcast(podcast: Podcast)

    fun setActivePodcast(podcastId: Int, active: Int?)

    fun getActivePodcast(): Flow<Podcast>

    suspend fun getActivePodcastSus(podcastId: Int): Podcast?

    fun setFinishPodcast(podcstId: Int)

    fun updatePodcastLastPos(podcastId: Int, position: Long)

    fun setNumPodcsts(num: Int)

    fun getPrefActivePodcastNum(): Flow<Int>

    fun getPrefActivePodcast(): Flow<Podcast>

    fun setPrefActivePodcastNum(num: Int)

    fun setPrefListType(type:ListViewType)

    fun getPrefListType(): Flow<ListViewType>

    fun setPrefLastPtime(time:Long)

    fun getPrefLastPtime(): Flow<Long>

    suspend fun PrefLastPtime(): Long?

    fun setPrefNumOnPage(num: Int)

    fun setPrefSelectedYear(year: Year)

//    fun getLastNPodcastListFlow(num: Int): Flow<List<Podcast>>
//
//    fun getNPodcastsListFromCurrent(num: Int, time: Long): Flow<List<Podcast>>

    fun numberTypeList(time: Long): Flow<List<Podcast>>

    fun yearTypeList(): Flow<List<Podcast>>

    fun updateTrackDuration(podcastId: Int, duration: Long)

    fun updateTrackIdDetailed(podcastId: Int, isDetailed: Boolean)

    fun getNumbAndTime(): Flow<PodcastLoadInfo>
//    fun getPodcastByYearFlow(year: Year): Flow<List<Podcast>>
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