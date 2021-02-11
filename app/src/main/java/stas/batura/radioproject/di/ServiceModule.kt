package stas.batura.radioproject.di

import android.content.Context
import android.provider.Settings.Global.getString
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import stas.batura.radioproject.R
import stas.batura.radioproject.data.IRepository
import stas.batura.radioproject.data.Repository
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
abstract class MusicServiceModule {

    @Binds
    abstract fun bindReposService(repositoryS: Repository): IRepository
}

@Module
@InstallIn(ServiceComponent::class)
class MusicServiceUtils
{

    @Provides
    fun provideDatabaseFactory(@ApplicationContext context: Context): DataSource.Factory {

        val httpDataSourceFactory: DataSource.Factory =
            OkHttpDataSourceFactory(
                OkHttpClient(),
                Util.getUserAgent(
                    context,
                    context.getString(R.string.app_name)
                )
            )


        val cache =
            SimpleCache(
                File(context.cacheDir.absolutePath + "/exoplayer"),
                LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100)
            ) // 100 Mb max

        val dataSourceFactory = CacheDataSourceFactory(
            cache,
            httpDataSourceFactory,
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )

        return dataSourceFactory
    }

    @Provides
    fun providesMediaSession(@ApplicationContext context: Context): MediaSessionCompat {

        return MediaSessionCompat(context,"Music Service")
    }

}