package droidkaigi.github.io.challenge2019.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.domain.hackernews.Entry
import droidkaigi.github.io.challenge2019.domain.hackernews.EntryId
import droidkaigi.github.io.challenge2019.util.toEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface EntryRepository {

    suspend fun loadTopStories(): LiveData<Resource<List<Entry>>>

    suspend fun getStory(id: EntryId): LiveData<Resource<Entry>>

}

class EntryRepositoryImpl(
    val api: HackerNewsApi
) : EntryRepository {
    override suspend fun loadTopStories(): LiveData<Resource<List<Entry>>> = coroutineScope {
        val liveData = MutableLiveData<Resource<List<Entry>>>().also {
            it.postValue(Loading())
        }
        try {
            val storyIds = (api.getTopStories().execute().body() ?: emptyList()).map(::EntryId)
            val jobs = storyIds.map { id ->
                async {
                    fetchStory(id)
                }
            }
            val stories = jobs.awaitAll()
            liveData.postValue(Success(stories.mapNotNull {
                when (it) {
                    is Success -> it.response
                    else -> null
                }
            }))
        } catch (e: Throwable) {
            liveData.postValue(Failure(e))
        }
        liveData
    }

    override suspend fun getStory(id: EntryId): LiveData<Resource<Entry>> = coroutineScope {
        val liveData = MutableLiveData<Resource<Entry>>().also {
            it.postValue(Loading())
        }
        liveData.postValue(async { fetchStory(id) }.await())
        liveData
    }

    private suspend fun fetchStory(id: EntryId): Resource<Entry> = coroutineScope {
        try {
            val body = api.getItem(id.id).execute().body()
            if (body != null) {
                Success(body.toEntry())
            } else {
                Failure<Entry>(Exception("No story for ${id.id}"))
            }
        } catch (e: Throwable) {
            Failure<Entry>(e)
        }
    }
}