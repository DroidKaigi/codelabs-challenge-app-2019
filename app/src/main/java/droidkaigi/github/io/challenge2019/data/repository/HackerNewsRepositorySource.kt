package droidkaigi.github.io.challenge2019.data.repository

import droidkaigi.github.io.challenge2019.data.model.Article
import droidkaigi.github.io.challenge2019.infrastructure.database.PreferencesProvider
import droidkaigi.github.io.challenge2019.infrastructure.network.HackerNewsApi
import javax.inject.Inject

class HackerNewsRepositorySource @Inject constructor(
    private val hackerNewsApi: HackerNewsApi,
    private val preferencesProvider: PreferencesProvider
) : HackerNewsRepository {
    override suspend fun fetchTopStories(): List<Article> {
        val response = hackerNewsApi.getTopStoriesNew().await().take(20)
        val items = response.map {
            hackerNewsApi.getItemNew(it)
        }
        val articleIds = preferencesProvider.getArticleIds()
        return items.map {
            val item = it.await()
            Article(item, articleIds.contains(item.id.toString()))
        }
    }

    override suspend fun fetchById(id: Long): Article {
        val articleIds = preferencesProvider.getArticleIds()
        val item = hackerNewsApi.getItemNew(id).await()
        return Article(item, articleIds.contains(item.id.toString()))
    }

    override suspend fun fetchByIds(ids: List<Long>): List<Article> {
        val items = ids.map {
            hackerNewsApi.getItemNew(it)
        }
        val articleIds = preferencesProvider.getArticleIds()
        return items.map {
            val item = it.await()
            Article(item, articleIds.contains(item.id.toString()))
        }
    }

    override suspend fun updateReadStatus(id: Long, alreadyRead: Boolean) {
        // TODO: 未読に戻せるようにするとか
        preferencesProvider.saveArticleIds(id.toString())
    }
}
