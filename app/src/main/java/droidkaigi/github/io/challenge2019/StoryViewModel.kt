package droidkaigi.github.io.challenge2019

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.data.repository.Resource
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story

class StoryViewModel: ViewModel() {

    // TODO: 4. Implement StoryViewModel
    // refer: https://developer.android.com/topic/libraries/architecture/livedata#transform_livedata

    // private val storyInput = MutableLiveData<Story>()
    val comments: LiveData<Resource<List<Comment?>>> =
        TODO("Implement this property")
        // Transformations.switchMap(storyInput) { story ->
        //    HackerNewsRepository.getComments(story)
        // }

    // private val isWebPageLoadingInput = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> =
        TODO("Implement this property")
        // MediatorLiveData<Boolean>().apply {
        //     addSource(comments) { resource ->
        //         value = resource is Resource.Loading || isWebPageLoadingInput.value ?: false
        //     }
        //     addSource(isWebPageLoadingInput) { isWebPageLoading ->
        //         value = isWebPageLoading ?: false || comments.value is Resource.Loading
        //     }
        //  }


    fun loadComments(story: Story) = {
        TODO("Implement this method")
        // storyInput.postValue(story)
    }

    fun onStartWebPageLoading() {
        TODO("Implement this method")
        // isWebPageLoadingInput.postValue(true)
    }

    fun onFinishWebPageLoading() {
        TODO("Implement this method")
    }

    fun onErrorWebPageLoading() {
        TODO("Implement this method")
    }
}