package stas.batura.radioproject

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.Menu
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.control_fragment_new.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import stas.batura.radioproject.musicservice.MusicService
import stas.batura.radioproject.utils.CircleTransform

private lateinit var appBarConfiguration: AppBarConfiguration

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val music = MusicService()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_podcastlist), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // загружаем хидер
        loadNavHeader()

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
            }
        }

        mainActivityViewModel.callbackChanges.observe(this, Observer {
            if (it != null) {
                if (it.state == PlaybackStateCompat.STATE_PLAYING) {
//                    Log.d(TAG, "onCreate: play spinner visible")
                    mainActivityViewModel._spinnerPlay.value = true
                } else if (it.state == PlaybackStateCompat.STATE_PAUSED ) {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel._spinnerPlay.value = false
                } else if (it.state == PlaybackStateCompat.STATE_NONE) {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel._spinnerPlay.value = false
                } else {
//                    Log.d(TAG, "onCreate: play spinner not visible")
                    mainActivityViewModel._spinnerPlay.value = false
                }
            }
        })

//        mainActivityViewModel.activePodcast.observe(this, {
//            Log.d(TAG, "onCreate:active podcast $it")
//            mainActivityViewModel.updateActivePodcast(it)
//        })

        mainActivityViewModel.smallCheck.observe(this) {
            Log.d(TAG, "onCreate: small bol= $it")
            if (it != null) {
                mainActivityViewModel.setPodcastIsSmall(it)
            }
        }

        // нициализируем сервис
        mainActivityViewModel.initMusicService()
    }



    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private fun loadNavHeader() { // name, website
        val navView = nav_view.getHeaderView(0)
        navView.name.text = ("Stanislav Batura")
        navView.website.text = ("stanislav.batura85@gmail.com")
        navView.img_header_bg.setImageResource(R.drawable.drawer_back)
        Glide.with(this).load(R.drawable.cat_my).transform(CircleTransform(this))
            .into(navView.img_profile)
    }

    private fun addNavViewActions() {
        //        // устанавливаем слушатель на нажатие клавиш
        nav_view.setNavigationItemSelectedListener( (NavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
//                    drawer_layout.closeDrawers()
                    true
                }



                else -> false
            }
        }) )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)

        //        addTour()
        return true
    }

    /**
     * прописывает базовое нажатие на открытие NavView
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    private fun bindCurrentService(serviceConnection: ServiceConnection) {
        // привязываем сервис к активити
        bindService(
            Intent(applicationContext!!, MusicService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE)
    }


}