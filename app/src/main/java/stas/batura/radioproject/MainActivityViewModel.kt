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
import androidx.lifecycle.*
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import stas.batura.radioproject.musicservice.MusicService
import stas.batura.radioproject.data.IRepository
import stas.batura.radioproject.data.ListViewType
import stas.batura.radioproject.data.dataUtils.Year
import stas.batura.radioproject.data.room.Podcast

class MainActivityViewModel @ViewModelInject constructor(
    val repository: IRepository
    , val application: Application
) : ViewModel() {

    private val TAG = MainActivityViewModel::class.java.simpleName

//    var numberLive = repository.em

    // binder instance
    var playerServiceBinder: MusicService.PlayerServiceBinder? = null

    // media controller for interaction
    val mediaController: MutableLiveData<MediaControllerCompat?> = MutableLiveData()

    // getting changes from service
    private var callback: MediaControllerCompat.Callback? = null

    // checking connection
    val serviceConnection: MutableLiveData<ServiceConnection?> = MutableLiveData(null)

    val exoPlayer: MutableLiveData<ExoPlayer> = MutableLiveData()

    val callbackChanges: MutableLiveData<PlaybackStateCompat?> = MutableLiveData(null)

    private var _createServiceListner: MutableLiveData<Boolean> = MutableLiveData(false)
    val createServiceListner: LiveData<Boolean>
        get() = _createServiceListner

    val activePodcast = repository.getActivePodcast().asLiveData()

    val activePodcastPref: MutableLiveData<Podcast?> = MutableLiveData(null)

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val smallCheck = MutableLiveData<Boolean?> (null)

    var _spinnerPlay: MutableLiveData<Boolean> = MutableLiveData(false)
    val spinnerPlay: LiveData<Boolean>
        get() = _spinnerPlay

    init {
        Log.d(TAG, "view model created: ")
    }

    fun createService() {
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

    /**
     * нажали плей
     */
    fun playClicked() {
        if (mediaController.value != null) {
            mediaController.value!!.transportControls.play()
        }
    }

    /**
     * нажали паузу
     */
    fun pauseClicked() {
        if (mediaController.value != null) {
            mediaController.value!!.transportControls.pause()
        }
    }

    /**
     * изменяем состояние кнопки
     */
    fun changePlayState() {
        if (mediaController.value != null && callbackChanges.value != null) {
            if (callbackChanges.value!!.state == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.value!!.transportControls.pause()
            } else {
                mediaController.value!!.transportControls.play()
            }
        }
    }

    fun movingPlayToPosition(position: Long, podcast: Podcast) {
        if (callbackChanges.value != null && callbackChanges.value!!.state.equals(
                PlaybackStateCompat.STATE_PLAYING
            )
        ) {
            mediaController.value!!.transportControls.stop()
        }

        var lastId: Int? = null
        if (activePodcast.value != null) {
            lastId = activePodcast.value!!.podcastId
        }
//        repository.setActivePodcast(podcastId = podcast.podcastId, active =  lastId)
        setActiveNumberPref(podcast.podcastId)
        playerServiceBinder!!.setPodcastWithPosition(podcast, position)
        playClicked()
    }

    fun updatePrefPodcastNum(num: Int) {
        repository.setPrefListType(ListViewType.NUMBER)

        repository.setNumPodcsts(num)
    }

    fun getPodcasttsInYear(year: Year) {
        repository.setPrefListType(ListViewType.YEAR)

        viewModelScope.launch {
            repository.getPodcastByYear(year)
        }
    }

    fun setPodcastIsSmall(bol: Boolean) {
        repository.setPrefPodcastIsSmall(bol)
    }

    fun setCheckBoxInitState(boolean: Boolean) {
        smallCheck.value = boolean
    }

    fun setActiveNumberPref(number: Int) {
        repository.setPrefActivePodcastNum(number)
    }

    fun updateActivePodcast(num: Int) {
        viewModelScope.launch {
            val podcast = repository.getActivePodcastSus(num)
            activePodcastPref.value = podcast
        }
    }

}