package droidkaigi.github.io.challenge2019

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import droidkaigi.github.io.challenge2019.data.repository.Resource
import droidkaigi.github.io.challenge2019.model.Story

class MainViewModel : ViewModel() {

    private val topStoriesRequest = MutableLiveData<Unit>()
    val topStories: LiveData<Resource<List<Story?>>> =
        Transformations.switchMap(topStoriesRequest) {
            HackerNewsRepository.getTopStories()
        }

    private val storyIdInput = MutableLiveData<Long>()
    val story: LiveData<Resource<Story>> =
        Transformations.switchMap(storyIdInput) { storyId ->
            HackerNewsRepository.getStory(storyId)
        }

    fun loadTopStories() = topStoriesRequest.postValue(Unit)
    fun loadStory(id: Long) = storyIdInput.postValue(id)
}