package stas.batura.radioproject.di

//import android.content.Context
//import com.google.android.exoplayer2.database.ExoDatabaseProvider
//import com.google.android.exoplayer2.offline.DownloadManager
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
//import com.google.android.exoplayer2.upstream.cache.Cache
//import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
//import com.google.android.exoplayer2.upstream.cache.SimpleCache
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
//import dagger.hilt.android.qualifiers.ApplicationContext
//import java.io.File
//import java.util.concurrent.Executor
//
//
//private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
//
//@InstallIn(ApplicationComponent::class)
//class DownloadServiceModule() {
//
////    fun provideDownloadManager(@ApplicationContext context: Context): DownloadManager {
////        // Note: This should be a singleton in your app.
////        // Note: This should be a singleton in your app.
////        val databaseProvider = ExoDatabaseProvider(context)
////
////// A download cache should not evict media, so should use a NoopCacheEvictor.
////
////// A download cache should not evict media, so should use a NoopCacheEvictor.
////        val downloadCache = getDownloadCache(context, databaseProvider)
////
////// Create a factory for reading the data from the network.
////
////// Create a factory for reading the data from the network.
////        val dataSourceFactory = DefaultHttpDataSourceFactory()
////
////// Choose an executor for downloading data. Using Runnable::run will cause each download task to
////// download data on its own thread. Passing an executor that uses multiple threads will speed up
////// download tasks that can be split into smaller parts for parallel execution. Applications that
////// already have an executor for background downloads may wish to reuse their existing executor.
////
////// Choose an executor for downloading data. Using Runnable::run will cause each download task to
////// download data on its own thread. Passing an executor that uses multiple threads will speed up
////// download tasks that can be split into smaller parts for parallel execution. Applications that
////// already have an executor for background downloads may wish to reuse their existing executor.
////        val downloadExecutor = Executor { obj: Runnable -> obj.run() }
////
////// Create the download manager.
////
////// Create the download manager.
////        val downloadManager = DownloadManager(
////            context,
////            databaseProvider,
////            downloadCache,
////            dataSourceFactory,
////        )
////        return downloadManager
////    }
////
////
////    @Synchronized
////    private fun getDownloadCache(context: Context, provider: ExoDatabaseProvider): Cache? {
////            val downloadContentDirectory: File = File(
////                getDownloadDirectory(context),
////                DOWNLOAD_CONTENT_DIRECTORY
////            )
////            val downloadCache = SimpleCache(
////                downloadContentDirectory,
////                NoOpCacheEvictor(),
////                provider
////            )
////        return downloadCache
////    }
////
////    @Synchronized
////    private fun getDownloadDirectory(context: Context): File? {
////
////        return context.filesDir
////    }
//
//}