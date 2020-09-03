package stas.batura.radioproject.data.net

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path


interface IRetrofit {
    @GET("/podcast/{num}")
    fun getPodcastByNum(@Path("num") num: String):
            Flow<PodcastBody>

}