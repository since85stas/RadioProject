package stas.batura.radioproject.data.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.batura.stat.batchat.repository.room.RadioDao
import ru.batura.stat.batchat.repository.room.RadioDatabase

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RadioDaoTests {

    private lateinit var radioDao: RadioDao

    private val testDispatcher = TestCoroutineDispatcher()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val coroutineScope =  MainCoroutineScopeRule()

    val podcast1 = Podcast(1, "url1", "title1", "time1")
    val podcast2 = Podcast(2, "url2", "title2", "time2")
    val podcast3 = Podcast(3, "url3", "title3", "time3")

    @Before
    fun setUp() {


        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        radioDao = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RadioDatabase::class.java
        ).allowMainThreadQueries().build().radioDatabaseDao

        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun check_lastPodcstValue_ok() = runBlocking{

            radioDao.insertPodcast(podcast1)
            radioDao.insertPodcast(podcast2)
            radioDao.insertPodcast(podcast3)

            val last = radioDao.getLastPodcast()
            assertEquals(podcast3, last)
    }




}