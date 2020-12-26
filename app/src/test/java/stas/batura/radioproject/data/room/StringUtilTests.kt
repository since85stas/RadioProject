package stas.batura.radioproject.data.room

import junit.framework.Assert
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import stas.batura.radioproject.data.dataUtils.getLinksFromHtml

class StringUtilTests {

    @Before
    fun init() {

    }

    @Test
    fun getFirstLink_from_string() {
         val firstLink = "https://arstechnica.com/gadgets/2020/12/microsoft-may-be-developing-its-own-in-house-arm-cpu-designs/"
        assertEquals(firstLink, getLinksFromHtml(testString,1)!![0])
    }

    @Test
    fun getLastLink_from_string() {
        val firstLink = "https://tip.golang.org/doc/go1.16"

        assertEquals(firstLink, getLinksFromHtml(testString,4)!![3])
    }

    @Test
    fun getLink_from_NullString() {
        val firstLink = null

        assertEquals(firstLink, getLinksFromHtml(null,4))
    }

    private var testString = "<p><img src=\"https://radio-t.com/images/radio-t/rt733.jpg\" alt=\"\" /></p>\n" +
            "\n" +
            "<ul>\n" +
            "<li><a href=\"https://arstechnica.com/gadgets/2020/12/microsoft-may-be-developing-its-own-in-house-arm-cpu-designs/\">Microsoft тоюе хочет ARM CPU designs</a> - <em>00:02:19</em>.</li>\n" +
            "<li><a href=\"https://www.wsj.com/articles/hack-suggests-new-scope-sophistication-for-cyberattacks-11608251360\">Атака от русских хакеров</a> - <em>00:12:08</em>.</li>\n" +
            "<li><a href=\"https://status.cloud.google.com/incident/zall/20013\">Как падал Google</a> - <em>00:21:15</em>.</li>\n" +
            "<li><a href=\"https://tip.golang.org/doc/go1.16\">Go 1.16 на подходе</a> - <em>00:29:46</em>.</li>\n"
}