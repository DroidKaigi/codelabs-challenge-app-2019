package droidkaigi.github.io.challenge2019

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.data.repository.Resource
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story

class StoryViewModel: ViewModel() {

    // TODO: 4. Implement StoryViewModel
    // refer: https://developer.android.com/topic/libraries/architecture/livedata#transform_livedata

    private val storyInput = MutableLiveData<Story>()
    val comments: LiveData<Resource<List<Comment?>>> =
        TODO("Implement this property")

    private val isWebPageLoadingInput = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(comments) { resource ->
            value = resource is Resource.Loading || isWebPageLoadingInput.value ?: false
        }
        addSource(isWebPageLoadingInput) { isWebPageLoading ->
            value = isWebPageLoading ?: false || comments.value is Resource.Loading
        }
    }

    fun loadComments(story: Story) = {
        TODO("Implement this method")
    }

    fun onStartWebPageLoading() = isWebPageLoadingInput.postValue(true)
    fun onFinishWebPageLoading() = isWebPageLoadingInput.postValue(false)
    fun onErrorWebPageLoading() = isWebPageLoadingInput.postValue(false)
}