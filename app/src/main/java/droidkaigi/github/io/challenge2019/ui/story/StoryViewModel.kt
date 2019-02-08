package droidkaigi.github.io.challenge2019.ui.story

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import droidkaigi.github.io.challenge2019.core.data.api.response.Item
import droidkaigi.github.io.challenge2019.core.data.model.Story
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoryViewModel @Inject constructor(
    private val hackerNewsRepository: HackerNewsRepository
): ViewModel() {

    val isCommentLoading = MutableLiveData<Boolean>()
    val isWebLoading = MutableLiveData<Boolean>()

    val comments = MutableLiveData<List<Item>>()

    fun getComments(story: Story) {
        isCommentLoading.value = true

        viewModelScope.launch {
            comments.value = hackerNewsRepository.getComments(story)
            // TODO: エラー対応

            isCommentLoading.value = false
        }
    }
}