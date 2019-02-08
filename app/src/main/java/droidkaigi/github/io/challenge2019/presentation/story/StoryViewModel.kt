package droidkaigi.github.io.challenge2019.presentation.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import droidkaigi.github.io.challenge2019.data.model.Comment
import droidkaigi.github.io.challenge2019.data.model.Story
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class StoryViewModel(
    private val repository: HackerNewsRepository,
    private val story: Story
) : ViewModel(), CoroutineScope {

    private val _comments: MutableLiveData<List<Comment>> = MutableLiveData()
    val comments: LiveData<List<Comment>> = _comments

    private val _commentLoading: MutableLiveData<Boolean> = MutableLiveData()
    val commentLoading: LiveData<Boolean> = _commentLoading

    val webViewLoading: MutableLiveData<Boolean> = MutableLiveData()

    // TODO: Errorハンドリング手抜き
    private val _errorEvent: MutableLiveData<Throwable> = MutableLiveData()
    val errorEvent: LiveData<Throwable> = _errorEvent

    private val job = Job()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _errorEvent.value = exception
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        onInit()
    }

    fun onInit() {
        launch(exceptionHandler) {
            _commentLoading.postValue(true)
            _comments.postValue(repository.fetchCommentsByIds(story.commentIds))
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
        private val story: Story
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = StoryViewModel(repository, story) as T
    }
}
