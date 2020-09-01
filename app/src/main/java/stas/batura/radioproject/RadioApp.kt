package stas.batura.radioproject

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RadioApp : Application() {
    init {
        print("App starting")
    }
}