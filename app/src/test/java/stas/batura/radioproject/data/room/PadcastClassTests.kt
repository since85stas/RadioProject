package stas.batura.radioproject.data.room

import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.experimental.theories.suppliers.TestedOn
import stas.batura.radioproject.data.dataUtils.DateTime
import stas.batura.radioproject.data.dataUtils.TIME_WEEK
import stas.batura.radioproject.data.dataUtils.getMillisTime
import stas.batura.radioproject.data.room.Podcast
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class PadcastClassTests {

    private val currPodcast = Podcast(
        podcastId = 10,
        url = "https://radio-t.com/p/2020/09/05/podcast-718/",
        time = "2020-09-05T23:41:16Z",
        title = "Радио-Т 718"
    )

    @Before
    fun init() {
    }


    @Test
    fun getPlayedInPersent() {
        val podcast = Podcast(podcastId = 1,durationInMillis = 10000L, lastPosition = 5000L)
        assertEquals(50, podcast.getPlayedInPercent())

        val podcast2 = Podcast(podcastId = 1,durationInMillis = 10000L, lastPosition = 4950L)
        assertEquals(50, podcast2.getPlayedInPercent())

        val podcast3 = Podcast(podcastId = 1,durationInMillis = 10000L, lastPosition = 40L)
        assertEquals(0, podcast3.getPlayedInPercent())

        val podcast4 = Podcast(podcastId = 1,durationInMillis = 10000L, lastPosition = 9950L)
        assertEquals(100, podcast4.getPlayedInPercent())

        val podcast5 = Podcast(podcastId = 1,durationInMillis = 10000L, lastPosition = 0L)
        assertEquals(0, podcast5.getPlayedInPercent())

        val podcast6 = Podcast(podcastId = 1,durationInMillis = 10000L, lastPosition = 11000L)
        assertEquals(100, podcast6.getPlayedInPercent())
    }

    @Test
    fun test_thatweekIsGonefun_ok() {
        assertEquals(true, currPodcast.isWeekGone(getMillisTime(currPodcast.time) + TIME_WEEK + 10L))
        assertEquals(true, currPodcast.isWeekGone(getMillisTime(currPodcast.time) + TIME_WEEK + 1000000L))
        assertEquals(false, currPodcast.isWeekGone(getMillisTime(currPodcast.time) + TIME_WEEK - 10L))
        assertEquals(false, currPodcast.isWeekGone(getMillisTime(currPodcast.time) + TIME_WEEK - 10000L))
    }

    @Test
    fun test_thatNumweekIsGone_ok() {
        assertEquals(1, currPodcast.numWeekGone(getMillisTime(currPodcast.time) + TIME_WEEK + 10L))
        assertEquals(3, currPodcast.numWeekGone(getMillisTime(currPodcast.time) + 3*TIME_WEEK + 10L))
        assertEquals(2, currPodcast.numWeekGone(getMillisTime(currPodcast.time) + 3*TIME_WEEK - 10L))
        assertEquals(0, currPodcast.numWeekGone(0))
    }

    @Test
    fun test_timeLableToTimeString() {
        assertEquals("00:00:12", setTrackDuratNative(12000))
        assertEquals("00:00:12", setTrackDuratNative(12500))
        assertEquals("00:01:00", setTrackDuratNative(60500))
        assertEquals("00:10:00", setTrackDuratNative(600500))
    }
}