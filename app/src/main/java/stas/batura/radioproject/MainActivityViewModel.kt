package stas.batura.radioproject

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import stas.batura.radioproject.musicservice.MusicService
import stas.batura.radioproject.data.IRepository
import stas.batura.radioproject.data.room.Podcast

class MainActivityViewModel @ViewModelInject constructor(val repository: IRepository
                                                         , val application: Application

): ViewModel() {

    private val TAG = MainActivityViewModel::class.java.simpleName

    // binder instance
    var playerServiceBinder: MusicService.PlayerServiceBinder? = null

    // media controller for interaction
    val mediaController: MutableLiveData<MediaControllerCompat?> = MutableLiveData()

    // getting changes from service
    private var callback: MediaControllerCompat.Callback? = null

    // checking connection
    val serviceConnection: MutableLiveData<ServiceConnection?> = MutableLiveData(null)

    val exoPlayer: MutableLiveData<ExoPlayer> = MutableLiveData()

    val callbackChanges : MutableLiveData<PlaybackStateCompat?> = MutableLiveData(null)

    private var _createServiceListner : MutableLiveData<Boolean> = MutableLiveData(false)
    val createServiceListner : LiveData<Boolean>
        get() = _createServiceListner

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Log.d(TAG, "view model created: ")
    }

    fun createService()  {
        _createServiceListner.value = true
        _createServiceListner.value = false
    }

    /**
     * создает музыкальный сервис и его контроллер
     */
    fun initMusicService() {
        if (serviceConnection.value == null) {
            // привязываем колбека и лайв дэйта
            callback = object : MediaControllerCompat.Callback() {
                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    callbackChanges.value = state
                }
            }

            // соединение с сервисом
            serviceConnection.value = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    playerServiceBinder = service as MusicService.PlayerServiceBinder
                    try {
                        mediaController.value = MediaControllerCompat(
                                application,
                        playerServiceBinder!!.getMediaSessionToke()
                        )

                        exoPlayer.value = playerServiceBinder!!.getPlayer()

                        mediaController.value!!.registerCallback(callback!!)
                        callback!!.onPlaybackStateChanged(mediaController.value!!.playbackState)
                    } catch (e: RemoteException) {
                        mediaController.value = null
                    }
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    playerServiceBinder = null
                    if (mediaController.value != null) {
                        mediaController.value!!.unregisterCallback(callback!!)
                        mediaController.value = null
                    }
                }
            }
        }
    }

    fun playClicked () {
        if (mediaController.value != null) {
            mediaController.value!!.transportControls.play()
        }
    }

    fun pauseClicked () {
        if (mediaController.value != null) {
            mediaController.value!!.transportControls.pause()
        }
    }

    fun preparingPlaying(podcast: Podcast) {
        playerServiceBinder!!.setPodcast(podcast)

        playClicked()
    }
}