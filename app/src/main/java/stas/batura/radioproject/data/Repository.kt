package stas.batura.radioproject.data

import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import stas.batura.radioproject.data.net.StatusResponse
import stas.batura.radioproject.data.room.Podcast

class Repository (): IRepository {

    private val TAG = Repository::class.java.simpleName

//    override fun getSongText(title: String): Deferred<StatusResponse> {
//        Log.d(TAG, "test")
//        return
//    }

    override fun insertPodcast(podcast: Podcast): Long {
        TODO("Not yet implemented")
    }

    override fun getPodcastsList(): Flow<List<Podcast>> {
        TODO("Not yet implemented")
    }
}