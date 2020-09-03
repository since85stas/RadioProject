package stas.batura.radioproject.ui.podcastlist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import stas.batura.radioproject.data.IRepository
import stas.batura.radioproject.data.Repository
import stas.batura.radioproject.data.room.Podcast

class PodcastListViewModel @ViewModelInject constructor(val repository: IRepository): ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    val text: LiveData<String> = _text

    val podcasts: LiveData<List<Podcast>> = repository.getPodcastsList().asLiveData()

    fun addPodcast() {
        repository.addPodcast(Podcast())
    }


}