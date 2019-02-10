package droidkaigi.github.io.challenge2019.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.domain.hackernews.EntryId
import droidkaigi.github.io.challenge2019.domain.hackernews.Story
import droidkaigi.github.io.challenge2019.util.toEntry
import kotlinx.coroutines.*

interface EntryRepository {

    suspend fun loadTopStories(): Resource<List<Story>>

    suspend fun getStory(id: EntryId): Resource<Story>

}

class EntryRepositoryImpl(
    val api: HackerNewsApi
) : EntryRepository {
    override suspend fun loadTopStories(): Resource<List<Story>> = coroutineScope {
        try {
            val storyIds = (api.getTopStories().execute().body() ?: emptyList()).map(::EntryId)
            val jobs = storyIds.map { id ->
                async {
                    fetchStory(id)
                }
            }
            val stories = jobs.awaitAll()
            Success(stories.mapNotNull {
                when (it) {
                    is Success -> it.response
                    else -> null
                }
            })
        } catch (e: Throwable) {
            Failure<List<Story>>(e)
        }
    }

    override suspend fun getStory(id: EntryId): Resource<Story> = coroutineScope {
        withContext(Dispatchers.Default) { fetchStory(id) }
    }

    private suspend fun fetchStory(id: EntryId): Resource<Story> = coroutineScope {
        try {
            val body = api.getItem(id.id).execute().body()
            if (body != null) {
                Success(body.toEntry() as Story)
            } else {
                Failure<Story>(Exception("No story for ${id.id}"))
            }
        } catch (e: Throwable) {
            Failure<Story>(e)
        }
    }
}