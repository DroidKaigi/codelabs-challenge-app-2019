package droidkaigi.github.io.challenge2019.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import droidkaigi.github.io.challenge2019.core.data.api.response.Item
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val hackerNewsRepository: HackerNewsRepository
) : ViewModel() {

    val items = MutableLiveData<List<Item>>()

    fun loadTopStories() {
        viewModelScope.launch {
            items.value = hackerNewsRepository.getTopStories()

            // TODO: エラー対応
        }
    }
}
