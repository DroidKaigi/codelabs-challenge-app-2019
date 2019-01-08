package droidkaigi.github.io.challenge2019

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.data.repository.Resource
import droidkaigi.github.io.challenge2019.data.repository.entity.Story

class MainViewModel : ViewModel() {

    // TODO: 2. Implement MainViewModel
    // refer: https://developer.android.com/topic/libraries/architecture/livedata#transform_livedata

    val topStories: LiveData<Resource<List<Story?>>> =
        TODO("Implement this property")

    val story: LiveData<Resource<Story>> =
        TODO("Implement this property")

    fun loadTopStories() {
        TODO("Implement this method")
    }

    fun loadStory(id: Long) {
        TODO("Implement this method")
    }
}