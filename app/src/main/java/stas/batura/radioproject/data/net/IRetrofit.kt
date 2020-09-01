package stas.batura.radioproject.data.net

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path


interface IRetrofit {
    @GET("/lyrics/{title}")
    fun getSongText(@Path("title") title: String):
            Deferred<StatusResponse>

}