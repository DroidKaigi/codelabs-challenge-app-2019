package droidkaigi.github.io.challenge2019

import android.arch.lifecycle.*
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import droidkaigi.github.io.challenge2019.data.repository.Resource
import droidkaigi.github.io.challenge2019.model.StoryWithComments

class StoryViewModel : ViewModel() {

    private val storyIdInput = MutableLiveData<Long>()
    val storyWithComments: LiveData<Resource<StoryWithComments>> =
        Transformations.switchMap(storyIdInput) { storyId ->
            HackerNewsRepository.getStoryWithComments(storyId)
        }

    private val isWebPageLoadingInput = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(storyWithComments) { resource ->
            value = resource is Resource.Loading || isWebPageLoadingInput.value ?: false
        }
        addSource(isWebPageLoadingInput) { isWebPageLoading ->
            value = isWebPageLoading ?: false || storyWithComments.value is Resource.Loading
        }
    }

    fun loadStoryWithComments(storyId: Long) = storyIdInput.postValue(storyId)
    fun onStartWebPageLoading() = isWebPageLoadingInput.postValue(true)
    fun onFinishWebPageLoading() = isWebPageLoadingInput.postValue(false)
    fun onErrorWebPageLoading() = isWebPageLoadingInput.postValue(false)
}