package stas.batura.audio.musicservice

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.FileDataSource.FileDataSourceException
import com.google.android.exoplayer2.upstream.cache.*
import stas.batura.radioproject.MainActivity
import stas.batura.radioproject.data.room.Podcast
import stas.batura.radioproject.musicservice.MusicService
import java.io.File

class MusicService (): Service() {

    private val NOTIF_CHANNEL_NAME = "audio.stas.chanel"

    private val NOTIFICATION_ID = 404
    private val NOTIFICATION_DEFAULT_CHANNEL_ID = "default_channel"

    private val podcast: Podcast =  Podcast(1, "url1", "title1", "time1")

    // билдер для данных
    private val metadataBuilder  = MediaMetadataCompat.Builder()

    lateinit var cache: Cache

    // плэйбэк
    private val stateBuilder: PlaybackStateCompat.Builder =
        PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
        )

    private var mediaSession: MediaSessionCompat? = null

    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    private var isAudioFocusRequested = false

    var exoPlayer: SimpleExoPlayer? = null
    private var extractorsFactory: ExtractorsFactory? = null
    private var dataSourceFactory: DataSource.Factory? = null

//    lateinit var musicRepository: MusicRepository

    var fileDataSource : DataSource? = null

    override fun onCreate() {
        super.onCreate()

//        musicRepository = MusicRepository(InjectorUtils.provideRep(application))
//        musicRepository = InjectorUtils.provideMusicRep(application)

        println("Music service created")

        // создаем аудио менеджер
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // настраиваем уведомления
            @SuppressLint("WrongConstant") val notificationChannel =
                NotificationChannel(
                    NOTIFICATION_DEFAULT_CHANNEL_ID,
                    NOTIF_CHANNEL_NAME,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT
                )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            val audioAttributes =
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

            // запрос на аудио фокус
            audioFocusRequest =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setAudioAttributes(audioAttributes)
                    .build()
        }

        // создаем и настраиваем медиа сессию
        mediaSession = MediaSessionCompat(this,"Music Service")
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession!!.setCallback(mediaSessionCallback)

        val activityIntent = Intent(applicationContext, MainActivity :: class.java)

        // настраиваем активити
        mediaSession!!.setSessionActivity(
            PendingIntent.getActivity(
                applicationContext,
                0,
               activityIntent,
                0
            )
        )

        val mediaButtonIntent = Intent(
            Intent.ACTION_MEDIA_BUTTON, null, applicationContext,
            MediaButtonReceiver::class.java
        )

        // настраиваем получатель кнопок
        mediaSession!!.setMediaButtonReceiver(
            PendingIntent.getBroadcast(
                applicationContext,
                0,
                mediaButtonIntent,
                0
            )
        )

        // создаем плеер
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            this,
            DefaultRenderersFactory(this),
            DefaultTrackSelector(),
            DefaultLoadControl()
        )

        // добавляем слушатель
        exoPlayer!!.addListener(exoPlayerListener)

//        val httpDataSourceFactory: DataSource.Factory =
//            OkHttpDataSourceFactory(
//                OkHttpClient(),
//                Util.getUserAgent(
//                    this,
//                    getString(R.string.app_name)
//                )
//            )

        val testUri =
            Uri.fromFile(File(Environment.getExternalStorageDirectory().absolutePath +
                    "/Music/Moonspell/Studio and Compilation/1995-Wolfheart (Original 1CD Release)/02 Love Crimes.mp3"))

        val dataSpec = DataSpec(testUri)
        fileDataSource =
            FileDataSource()
        try {
            fileDataSource!!.open(dataSpec)
        } catch (e: FileDataSourceException) {
            e.printStackTrace()
        }


        val factory =
            DataSource.Factory { fileDataSource }

         cache =
            SimpleCache(
                File(this.cacheDir.absolutePath + "/exoplayer"),
                LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100)
            ) // 100 Mb max

        dataSourceFactory = CacheDataSourceFactory(
            cache,
            factory,
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )

//        val audioSource: MediaSource = ExtractorMediaSource(
//            fileDataSource!!.getUri(),
//            factory, DefaultExtractorsFactory(), null, null
//        )

        extractorsFactory = DefaultExtractorsFactory()

    }

    override fun unbindService(conn: ServiceConnection) {
        println("unbind service")
        super.unbindService(conn)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("on unbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        println("on reunbind")
        super.onRebind(intent)
    }

    /**
     * создаем колбэк для управления сервисом
     */
    private val  mediaSessionCallback = object : MediaSessionCompat.Callback() {

        private var currentUri: Uri? = null
        var currentState = PlaybackStateCompat.STATE_STOPPED

        // при подготовке сервиса
        override fun onPrepare() {
            super.onPrepare()
        }

        // при начале проигрыша
        override fun onPlay() {
            if (!exoPlayer!!.playWhenReady) {
                startService(
                    Intent(
                        applicationContext,
                        MusicService::class.java
                    )
                )
                if (!mediaSession!!.isActive) {
//                    val track: MusicRepository.Track = musicRepository.getCurrent()
                    updateMetadataFromTrack(podcast)
                    prepareToPlay(Uri.parse(podcast.audioUrl))
                    if (!isAudioFocusRequested) {
                        isAudioFocusRequested = true
                        var audioFocusResult: Int
                        audioFocusResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            audioManager!!.requestAudioFocus(audioFocusRequest!!)
                        } else {
                            audioManager!!.requestAudioFocus(
                                audioFocusChangeListener,
                                AudioManager.STREAM_MUSIC,
                                AudioManager.AUDIOFOCUS_GAIN
                            )
                        }
                        if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) return
                    }
                    mediaSession!!.isActive = true // Сразу после получения фокуса
                }
                registerReceiver(
                    becomingNoisyReceiver,
                    IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                )
                exoPlayer!!.playWhenReady = true
            }

            mediaSession!!.setPlaybackState(
                stateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                ).build()
            )
            currentState = PlaybackStateCompat.STATE_PLAYING

            refreshNotificationAndForegroundStatus(currentState)
        }

        // при остановки проигрыша
        override fun onPause() {
            if (exoPlayer!!.playWhenReady) {
                exoPlayer!!.playWhenReady = false
                unregisterReceiver(becomingNoisyReceiver)
            }

            mediaSession!!.setPlaybackState(
                stateBuilder.setState(
                    PlaybackStateCompat.STATE_PAUSED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                ).build()
            )
            currentState = PlaybackStateCompat.STATE_PAUSED

            refreshNotificationAndForegroundStatus(currentState)
        }



        // при остановки проигрыша
        override fun onStop() {
            if (exoPlayer!!.playWhenReady) {
                exoPlayer!!.playWhenReady = false
                unregisterReceiver(becomingNoisyReceiver)
            }

            if (isAudioFocusRequested) {
                isAudioFocusRequested = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager!!.abandonAudioFocusRequest(audioFocusRequest!!)
                } else {
                    audioManager!!.abandonAudioFocus(audioFocusChangeListener)
                }
            }

            mediaSession!!.isActive = false

            mediaSession!!.setPlaybackState(
                stateBuilder.setState(
                    PlaybackStateCompat.STATE_STOPPED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                ).build()
            )
            currentState = PlaybackStateCompat.STATE_STOPPED

            refreshNotificationAndForegroundStatus(currentState)

            stopSelf()
        }


        /**
         * играем по uri
         */
        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
//            val track = musicRepository.getTrackByUri(uri)
            updateMetadataFromTrack(podcast)

            refreshNotificationAndForegroundStatus(currentState)

            prepareToPlay(Uri.parse(podcast.url))
        }

        // подготавливаем трэк
        private fun prepareToPlay(uri: Uri) {
                currentUri = uri
                val mediaSource =
                    ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null)
                exoPlayer!!.prepare(mediaSource)
        }

        // обновляем данные о треке
        private fun updateMetadataFromTrack(podcast: Podcast) {
//            val image = Glide.with(this@MusicService).load(track.bitmapUri) as Bitmap
            if (podcast.url == null) {
//                metadataBuilder.putBitmap(
//                    MediaMetadataCompat.METADATA_KEY_ART,
////                    BitmapFactory.decodeResource(BitmapFactory.decodeFile(podcast.imageUrl))
//                )
            } else {
                metadataBuilder.putBitmap(
                    MediaMetadataCompat.METADATA_KEY_ART,
                    BitmapFactory.decodeFile(podcast.imageUrl)
                )
            }

            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, podcast.title)
//            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, podcast.)
//            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
//            metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration)
            mediaSession!!.setMetadata(metadataBuilder.build())

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession!!, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("Service bind")
        return PlayerServiceBinder()
    }

    // слушатель на плеер
    private val exoPlayerListener: Player.EventListener = object : Player.EventListener {
        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
        }

        override fun onLoadingChanged(isLoading: Boolean) {
        }

        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            if (playWhenReady && playbackState == ExoPlayer.STATE_ENDED) {
                mediaSessionCallback.onSkipToNext()
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        }
    }

    // отклик на фокус
    private val audioFocusChangeListener : OnAudioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange : Int ->
            when (focusChange ) {
                AudioManager.AUDIOFOCUS_GAIN -> mediaSessionCallback.onPlay() // Не очень красиво
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaSessionCallback.onPause()
                else -> mediaSessionCallback.onPause()
            }
        }

    //
    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) { // Disconnecting headphones - stop playback
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                mediaSessionCallback.onPause()
            }
        }
    }

    inner class PlayerServiceBinder : Binder () {

        fun  getMediaSessionToke() : MediaSessionCompat.Token{
            return mediaSession!!.sessionToken
        }

        fun getPlayer(): ExoPlayer? {
            return exoPlayer
        }


    }

    private fun refreshNotificationAndForegroundStatus(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> {
                startForeground(NOTIFICATION_ID, getNotification(playbackState))
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                NotificationManagerCompat.from(this@MusicService)
                    .notify(NOTIFICATION_ID, getNotification(playbackState)!!)
                stopForeground(false)
            }
            else -> {
                stopForeground(true)
            }
        }
    }

    private fun getNotification(playbackState: Int): Notification? {
        val builder =
            MediaStyleHelper.from(this, mediaSession)
        builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_previous,
                "prevoius",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
            )
        )
        if (playbackState == PlaybackStateCompat.STATE_PLAYING) builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )
        ) else builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )
        )
        builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_next,
                "next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                )
            )
        )
        builder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1)
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
                .setMediaSession(mediaSession!!.sessionToken)
        ) // setMediaSession требуется для Android Wear
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//        builder.color = ContextCompat.getColor(
//            this,
//            R.color.colorPrimaryDark
//        )
        // The whole background (in MediaStyle), not just icon background
        builder.setShowWhen(false)
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setOnlyAlertOnce(true)
        builder.setChannelId(NOTIFICATION_DEFAULT_CHANNEL_ID)
        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession!!.release()
        exoPlayer!!.release()
        cache.release()
//        cache = null
    }
}