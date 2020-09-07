package stas.batura.radioproject.data

import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import stas.batura.radioproject.data.dataUtils.DateTime
import stas.batura.radioproject.data.dataUtils.TIME_WEEK
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
    fun test_thatweekIsGonefun_ok() {
        assertEquals(true, currPodcast.isWeekGone(currPodcast.getMillisTime() + TIME_WEEK + 10L))
        assertEquals(true, currPodcast.isWeekGone(currPodcast.getMillisTime() + TIME_WEEK + 1000000L))
        assertEquals(false, currPodcast.isWeekGone(currPodcast.getMillisTime() + TIME_WEEK - 10L))
        assertEquals(false, currPodcast.isWeekGone(currPodcast.getMillisTime() + TIME_WEEK - 10000L))
    }
}