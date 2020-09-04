package ru.batura.stat.batchat.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import stas.batura.radioproject.data.room.Podcast

@Dao
interface RadioDao {

    @Insert
    suspend fun insertPodcast(podcast: Podcast): Long

    @Query("SELECT * FROM podcast_table ORDER BY podcastId")
    fun getPodcastsList(): Flow<List<Podcast>>

}