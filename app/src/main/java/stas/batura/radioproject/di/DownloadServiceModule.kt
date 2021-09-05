package stas.batura.radioproject.di

import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import stas.batura.radioproject.download.PodcastDownloadService.DOWNLOAD_NOTIFICATION_CHANNEL_ID
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton


private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

@Module
@InstallIn(ApplicationComponent::class)
class DownloadServiceModule() {

    @Provides
    @Singleton
    fun provideDatabseProvider(@ApplicationContext context: Context): ExoDatabaseProvider {
        return ExoDatabaseProvider(context)
    }

    @Provides
    @Singleton
    @Synchronized
    fun provideExoCache(
        @ApplicationContext context: Context,
        provider: ExoDatabaseProvider): Cache {
        val downloadContentDirectory: File = File(
            getDownloadDirectory(context),
            DOWNLOAD_CONTENT_DIRECTORY
        )
        val downloadCache = SimpleCache(
            downloadContentDirectory,
            NoOpCacheEvictor(),
            provider
        )
        return downloadCache
    }

    @Provides
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        dataSourceFactory: DataSource.Factory,
        downloadCache: Cache,
        databaseProvider: ExoDatabaseProvider
        ): DownloadManager {

        // Note: This should be a singleton in your app.
//        val databaseProvider = provideDatabseProvider(context)

        // A download cache should not evict media, so should use a NoopCacheEvictor.
//        val downloadCache = provideExoCache(context)


        // Choose an executor for downloading data. Using Runnable::run will cause each download task to
        // download data on its own thread. Passing an executor that uses multiple threads will speed up
        // download tasks that can be split into smaller parts for parallel execution. Applications that
        // already have an executor for background downloads may wish to reuse their existing executor.
        val downloadExecutor = Executor { obj: Runnable -> obj.run() }


        // Create the download manager.
        val downloadManager = DownloadManager(
            context,
            databaseProvider,
            downloadCache,
            dataSourceFactory,
            downloadExecutor
        )
        return downloadManager
    }

//    @Provides
//    fun provideDownloadHelper(@ApplicationContext context: Context,
//                              dataSourceFactory: DataSource.Factory
//    ) {
//        val helper =
//    }


//    @Synchronized
//    private fun getDownloadCache(context: Context, provider: ExoDatabaseProvider): Cache {
//        val downloadContentDirectory: File = File(
//            getDownloadDirectory(context),
//            DOWNLOAD_CONTENT_DIRECTORY
//        )
//        val downloadCache = SimpleCache(
//            downloadContentDirectory,
//            NoOpCacheEvictor(),
//            provider
//        )
//        return downloadCache
//    }

    @Provides
    fun providetDownloadNotificationHelper(@ApplicationContext context: Context): DownloadNotificationHelper {
        return DownloadNotificationHelper(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
    }

    @Synchronized
    private fun getDownloadDirectory(context: Context): File? {
        return context.filesDir
    }



}