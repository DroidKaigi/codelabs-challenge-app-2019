package droidkaigi.github.io.challenge2019.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import droidkaigi.github.io.challenge2019.core.data.model.Story
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val hackerNewsRepository: HackerNewsRepository
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val isRefreshing = MutableLiveData<Boolean>()

    val stories = MutableLiveData<List<Story>>()

    fun loadTopStories(refresh: Boolean = false) {
        if (!refresh && stories.value != null) {
            return
        }

        isLoading.value = !refresh
        isRefreshing.value = refresh

        viewModelScope.launch {
            stories.value = hackerNewsRepository.getTopStories()
            isLoading.value = false
            isRefreshing.value = false

            // TODO: エラー対応
        }
    }

    fun saveReadId(id: Long) {
        hackerNewsRepository.saveReadStoryId(id)
    }

    fun getReadIds(): Set<Long> {
        return hackerNewsRepository.getReadStoryIds()
    }
}
