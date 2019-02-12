package droidkaigi.github.io.challenge2019.presenter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import droidkaigi.github.io.challenge2019.domain.hackernews.Story
import droidkaigi.github.io.challenge2019.repository.EntryRepository
import droidkaigi.github.io.challenge2019.repository.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class StoriesViewModel(
    private val repository: EntryRepository
) : ViewModel(), CoroutineScope {

    private val _isLoading = MutableLiveData<Boolean>().also {
        it.value = false
    }
    val isLoading: LiveData<Boolean> = _isLoading
    val isFirstLoading: LiveData<Boolean> = Transformations.map(isLoading) { isLoading ->
        isLoading && stories.value == null
    }
    val stories: LiveData<Resource<List<Story>>> = Transformations.switchMap(isLoading) { isLoading ->
        val storiesData = MutableLiveData<Resource<List<Story>>>()
        if (isLoading) {
            launch {
                val stories = withContext(Dispatchers.Default) {
                    repository.loadTopStories()
                }
                storiesData.value = stories
                _isLoading.value = false
            }
        }
        storiesData
    }

    private val job = kotlinx.coroutines.Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    fun loadTopStories() {
        _isLoading.value = true
    }

}