package stas.batura.radioproject.data.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.batura.stat.batchat.repository.room.RadioDatabase

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RadioDaoTests {

    private lateinit var radioDB: RadioDatabase

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
        radioDB = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RadioDatabase::class.java
        ).allowMainThreadQueries().build()

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearOff() {
        radioDB.clearAllTables()
        radioDB.close()
    }

    @Test
    fun check_lastPodcstValue_ok() = runBlocking{

            radioDB.radioDatabaseDao.insertPodcast(podcast1)
            radioDB.radioDatabaseDao.insertPodcast(podcast2)
            radioDB.radioDatabaseDao.insertPodcast(podcast3)

            val last = radioDB.radioDatabaseDao.getLastPodcast()
            assertEquals(podcast3, last)
    }

    // TODO: разобраться как писать нормальные тесты
    @Test
    fun check_lastPodcastFlow_ok() = runBlocking {
        radioDB.radioDatabaseDao.insertPodcast(podcast1)
        radioDB.radioDatabaseDao.insertPodcast(podcast2)
        radioDB.radioDatabaseDao.insertPodcast(podcast3)

        val lastFlow = radioDB.radioDatabaseDao.getPodcastFlowByNum(1)

        var last: Podcast? = null

        val listlast = lastFlow.first()

        assertEquals(podcast1, listlast)
    }

    @Test
    fun check_lastPodcastFlowList_ok() = runBlocking {
        radioDB.radioDatabaseDao.insertPodcast(podcast1)
        radioDB.radioDatabaseDao.insertPodcast(podcast2)
        radioDB.radioDatabaseDao.insertPodcast(podcast3)

        val testList = mutableListOf<Podcast>()
        testList.add(podcast3)
        testList.add(podcast2)
        testList.add(podcast1)

        val lastFlow = radioDB.radioDatabaseDao.getPodcastsList()

        var last: Podcast? = null

        val listlast = lastFlow.first()

        assertEquals(testList, listlast!!)
    }
}