package stas.batura.radioproject.data.room

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import stas.batura.radioproject.data.net.IRetrofit
import stas.batura.radioproject.data.net.RetrofitClient

@Module
@InstallIn(ActivityComponent::class)
class RetrofitModule {

    @Provides
    fun providesRetrofitService(): IRetrofit {
        return RetrofitClient.netApi.servise
    }

}