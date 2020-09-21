package stas.batura.radioproject.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.batura.stat.batchat.repository.room.RadioDao
import stas.batura.radioproject.data.net.IRetrofit
import stas.batura.radioproject.data.room.Podcast

interface IRepository {

    suspend fun tryUpdateRecentRadioCache()

    fun addPodcast(podcast: Podcast)

    fun getPodcastsList(): Flow<List<Podcast>>

    fun setActivePodcast(podcastId: Int)

    fun getActivePodcast(): Flow<Podcast>
}