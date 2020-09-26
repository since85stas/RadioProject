package stas.batura.radioproject.data.dataUtils

import android.util.Log


fun getLinksFromHtml(body: String?): String {

    if (body != null) {

        // убираем все буквы, оставляем только номер
        val reg = "https^".toRegex()
        val num = reg.findAll(body)
        for (result in num) {
            Log.d("StringUtils", "getLinksFromHtml: " + result.value)
        }
        Log.d("StringUtils", "getLinksFromHtml:")
    }

    return ""
}