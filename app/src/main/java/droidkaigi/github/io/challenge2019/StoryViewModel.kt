package droidkaigi.github.io.challenge2019

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.data.repository.Resource
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story

class StoryViewModel: ViewModel() {

    // TODO: 4. Implement StoryViewModel
    // refer: https://developer.android.com/topic/libraries/architecture/livedata#transform_livedata

    val comments: LiveData<Resource<List<Comment?>>> =
        TODO("Implement this property")

    val isLoading: LiveData<Boolean> =
        TODO("Implement this property")


    fun loadComments(story: Story) = {
        TODO("Implement this method")
    }

    fun onStartWebPageLoading() {
        TODO("Implement this method")
    }

    fun onFinishWebPageLoading() {
        TODO("Implement this method")
    }

    fun onErrorWebPageLoading() {
        TODO("Implement this method")
    }
}