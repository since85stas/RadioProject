package stas.batura.radioproject.ui.podcastlist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import stas.batura.radioproject.data.Repository

class PodcastListViewModel @ViewModelInject constructor(val repository: Repository): ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }



    val text: LiveData<String> = _text


}