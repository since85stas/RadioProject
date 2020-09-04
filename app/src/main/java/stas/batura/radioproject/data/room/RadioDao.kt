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

    @Insert
    suspend fun insertPodcast(podcast: Podcast): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Podcast>)

    @Query("SELECT * FROM podcast_table ORDER BY podcastId")
    fun getPodcastsList(): Flow<List<Podcast>>

}