package ru.batura.stat.batchat.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import stas.batura.radioproject.data.room.Podcast

@Dao
interface RadioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPodcast(podcast: Podcast): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(plants: List<Podcast>)

    @Query("SELECT * FROM podcast_table ORDER BY podcastId DESC")
    fun getPodcastsList(): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE podcastId = :num")
    fun getPodcastFlowByNum(num: Int): Flow<Podcast>

    @Query("SELECT * FROM podcast_table WHERE podcastId = :num")
    suspend fun getPodcastByNum(num: Int): Podcast?

    @Query("SELECT * FROM podcast_table ORDER BY podcastId DESC")
    suspend fun getLastPodcast(): Podcast?

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertCategory(categories: Category)
}