package droidkaigi.github.io.challenge2019.presentation.main

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
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val repository: HackerNewsRepository) : ViewModel(), CoroutineScope {

    // TODO: Errorハンドリング手抜き

    private val _items: MutableLiveData<List<Item>> = MutableLiveData()
    val items: LiveData<List<Item>> = _items

    private val _item: MutableLiveData<Item> = MutableLiveData()
    val item: LiveData<Item> = _item

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        launch {
            _items.postValue(repository.fetchTopStories())
        }
    }

    fun onClickItem(id: Long) {
        launch {
            _item.postValue(repository.fetchById(id))
        }
    }

    fun onRefresh() {
        launch {
            _items.postValue(repository.fetchTopStories())
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
