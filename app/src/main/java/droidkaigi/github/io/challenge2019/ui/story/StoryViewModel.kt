package droidkaigi.github.io.challenge2019.ui.story

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import droidkaigi.github.io.challenge2019.core.data.api.response.Item
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoryViewModel @Inject constructor(
    private val hackerNewsRepository: HackerNewsRepository
): ViewModel() {

    val comments = MutableLiveData<List<Item>>()

    fun getComments(item: Item) {
        viewModelScope.launch {
            comments.value = hackerNewsRepository.getComments(item)
            // TODO: エラー対応
        }
    }
}