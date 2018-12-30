package droidkaigi.github.io.challenge2019

import android.arch.lifecycle.*
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import droidkaigi.github.io.challenge2019.data.repository.Resource
import droidkaigi.github.io.challenge2019.model.Comment
import droidkaigi.github.io.challenge2019.model.Story

class StoryViewModel : ViewModel() {

    private val storyInput = MutableLiveData<Story>()
    val comments: LiveData<Resource<List<Comment?>>> =
        Transformations.switchMap(storyInput) { story ->
            HackerNewsRepository.getComments(story)
        }
    private val isWebPageLoadingInput = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(comments) { resource ->
            value = resource is Resource.Loading || isWebPageLoadingInput.value ?: false
        }
        addSource(isWebPageLoadingInput) { isWebPageLoading ->
            value = isWebPageLoading ?: false || comments.value is Resource.Loading
        }
    }

    fun loadComments(story: Story) = storyInput.postValue(story)
    fun onStartWebPageLoading() = isWebPageLoadingInput.postValue(true)
    fun onFinishWebPageLoading() = isWebPageLoadingInput.postValue(false)
    fun onErrorWebPageLoading() = isWebPageLoadingInput.postValue(false)
}