package stas.batura.radioproject

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import stas.batura.radioproject.musicservice.MusicService
import stas.batura.radioproject.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val music = MusicService()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_podcastlist, R.id.navigation_chat))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

//        val bindings: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        bindings.lifecycleOwner = this
//        bindings.mainViewModel = mainActivityViewModel

        mainActivityViewModel.createServiceListner.observe(this) {it ->
            if (it) {
                mainActivityViewModel.initMusicService()
            }
        }

        mainActivityViewModel.serviceConnection.observe(this) {it ->
            if (it != null) {
                Log.d(TAG, "onCreate: " + it.toString())
                bindCurrentService(it)

//                mainActivityViewModel.playClicked()
            }
        }
    }

    private fun bindCurrentService(serviceConnection: ServiceConnection) {
        // привязываем сервис к активити
        bindService(
            Intent(applicationContext!!, MusicService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE)
    }


}