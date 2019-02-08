package droidkaigi.github.io.challenge2019.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val hackerNewsRepository: HackerNewsRepository
): ViewModel() {

    fun loadTopStories() {
        viewModelScope.launch {
            val ids = hackerNewsRepository.getTopStories()
            Timber.d("$ids")
        }
    }
}
