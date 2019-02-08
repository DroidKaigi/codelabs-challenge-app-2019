package droidkaigi.github.io.challenge2019.presentation.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class StoryViewModel(
    private val repository: HackerNewsRepository,
    private val story: Item
) : ViewModel(), CoroutineScope {

    // TODO: Errorハンドリング手抜き
    private val _comments: MutableLiveData<List<Item>> = MutableLiveData()
    val comments: LiveData<List<Item>> = _comments

    private val _commentLoading: MutableLiveData<Boolean> = MutableLiveData()
    val commentLoading: LiveData<Boolean> = _commentLoading

    val webViewLoading: MutableLiveData<Boolean> = MutableLiveData()

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        launch {
            _commentLoading.postValue(true)
            _comments.postValue(repository.fetchByIds(story.kids))
        }.invokeOnCompletion {
            _commentLoading.postValue(false)
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    class Factory(
        private val repository: HackerNewsRepository,
        private val story: Item
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = StoryViewModel(repository, story) as T
    }
}
