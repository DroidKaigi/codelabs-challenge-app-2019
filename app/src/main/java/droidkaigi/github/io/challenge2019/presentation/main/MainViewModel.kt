package droidkaigi.github.io.challenge2019.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import droidkaigi.github.io.challenge2019.data.model.Article
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val repository: HackerNewsRepository) : ViewModel(), CoroutineScope {

    private val _articles: MutableLiveData<List<Article>> = MutableLiveData()
    val articles: LiveData<List<Article>> = _articles

    private val _article: MutableLiveData<Article> = MutableLiveData()
    val article: LiveData<Article> = _article

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading

    private val _refreshing: MutableLiveData<Boolean> = MutableLiveData()
    val refreshing: LiveData<Boolean> = _refreshing

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
            _loading.postValue(true)
            _articles.postValue(repository.fetchTopStories())
            _loading.postValue(false)
        }
    }

    fun onRefresh() {
        launch(exceptionHandler) {
            _refreshing.postValue(true)
            _articles.postValue(repository.fetchTopStories())
            _refreshing.postValue(false)
        }
    }

    fun onClickItem(id: Long) {
        launch(exceptionHandler) {
            _article.postValue(repository.fetchById(id))
        }
    }

    fun onReadArticle(id: Long) {
        launch(exceptionHandler) {
            repository.updateReadStatus(id, true)
            _article.postValue(repository.fetchById(id))
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    class Factory @Inject constructor(private val repository: HackerNewsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(repository) as T
    }
}
