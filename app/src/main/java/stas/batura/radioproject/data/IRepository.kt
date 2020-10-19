package stas.batura.radioproject.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.batura.stat.batchat.repository.room.RadioDao
import stas.batura.radioproject.UserPreferences
import stas.batura.radioproject.data.net.IRetrofit
import stas.batura.radioproject.data.room.Podcast

interface IRepository {

    suspend fun tryUpdateRecentRadioCache()

    fun addPodcast(podcast: Podcast)

    fun getAllPodcastsList(): Flow<List<Podcast>>

    fun getlastNPodcastsList(num: Int): Flow<List<Podcast>>

    fun setActivePodcast(podcastId: Int, active: Int?)

    fun getActivePodcast(): Flow<Podcast>

    fun setFinishPodcast(podcstId: Int)

    fun updatePodcastLastPos(podcastId: Long)

    fun emitNumber(num: Int)


    @ExperimentalCoroutinesApi
    fun obsNumber(): StateFlow<Int>

    fun getUserPref(): Flow<UserPreferences>

    fun setNumPodcsts(num: Int)

    @ExperimentalCoroutinesApi
    fun currentPodcList(): StateFlow<List<Podcast>?>

    suspend fun getAllPodcastListFlow()

    suspend fun getLastNPodcastListFlow(num: Int)
}