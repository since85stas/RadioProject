package ru.batura.stat.batchat.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import stas.batura.radioproject.data.room.Podcast
import java.net.URL

@Dao
interface RadioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPodcast(podcast: Podcast): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(plants: List<Podcast>)

    @Query("SELECT * FROM podcast_table ORDER BY podcastId DESC")
    fun getAllPodcastsList(): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table ORDER BY podcastId DESC LIMIT :num")
    fun getLastNPodcastsList(num: Int): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE time > :timeStart AND time < :timeEnd ORDER BY podcastId DESC")
    fun getPodcastsBetweenTimes(timeStart: Long, timeEnd: Long): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE podcastId = :num")
    fun getPodcastFlowByNum (num: Int): Flow<Podcast>

    @Query("SELECT * FROM podcast_table WHERE podcastId = :num")
    suspend fun getPodcastByNum (num: Int): Podcast?

    @Query("SELECT * FROM podcast_table ORDER BY podcastId DESC")
    suspend fun getLastPodcast(): Podcast?

    @Query("UPDATE podcast_table SET isActive = 1 WHERE podcastId = :podcastId")
    suspend fun setPodcastActive(podcastId: Int)

    @Query ("UPDATE podcast_table SET isActive = 0 WHERE podcastId = :podcastId")
    suspend fun setPodIsNOTActive (podcastId: Int)

    @Query ("UPDATE podcast_table SET isActive = 0")
    suspend fun setAllPodIsNOTActive()

    @Query ("SELECT * FROM podcast_table WHERE isActive = 1")
    fun getActivePodcast(): Flow<Podcast?>

    @Query("UPDATE podcast_table SET isFinish = 1 WHERE podcastId = :podcastId")
    suspend fun setPodcastFinish(podcastId: Int)

    @Query("UPDATE podcast_table SET lastPosition = podcastId WHERE podcastId = :podcastId")
    suspend fun updatePodcastLastPos(podcastId: Long)

}