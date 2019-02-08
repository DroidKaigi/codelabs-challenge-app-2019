package droidkaigi.github.io.challenge2019.data.repository

import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.infrastructure.network.HackerNewsApi
import javax.inject.Inject

class HackerNewsRepositorySource @Inject constructor(private val hackerNewsApi: HackerNewsApi) : HackerNewsRepository {
    override suspend fun fetchTopStories(): List<Item> {
        val response = hackerNewsApi.getTopStoriesNew().await().take(20)
        val items = response.map {
            hackerNewsApi.getItemNew(it)
        }
        return items.map { it.await() }
    }

    override suspend fun fetchById(id: Long): Item {
        return hackerNewsApi.getItemNew(id).await()
    }

    override suspend fun fetchByIds(ids: List<Long>): List<Item> {
        val items = ids.map {
            hackerNewsApi.getItemNew(it)
        }
        return items.map { it.await() }
    }
}
