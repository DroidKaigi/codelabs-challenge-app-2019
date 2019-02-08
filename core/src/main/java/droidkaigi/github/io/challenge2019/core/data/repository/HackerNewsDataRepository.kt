package droidkaigi.github.io.challenge2019.core.data.repository

import droidkaigi.github.io.challenge2019.core.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.core.data.api.response.Item
import droidkaigi.github.io.challenge2019.core.data.model.Comment
import droidkaigi.github.io.challenge2019.core.data.model.Story
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.IOException

class HackerNewsDataRepository(
    private val hackerNewsApi: HackerNewsApi
) : HackerNewsRepository {
    override suspend fun getTopStories(): List<Story> = withContext(Dispatchers.IO) {
        val call = hackerNewsApi.getTopStories()
        val response = call.execute()
        if (!response.isSuccessful) {
            // TODO
            throw IOException("Error")
        }
        val ids = response.body() ?: emptyList()

        ids.map { id ->
            async { getItem(id) }
        }.awaitAll().map {
            it.toStory()
        }
    }

    override suspend fun getComments(story: Story): List<Comment> = withContext(Dispatchers.IO) {
        story.commentIds.map { id ->
            async { getItem(id) }
        }.awaitAll().map {
            it.toComment()
        }
    }

    private fun getItem(id: Long): Item {
        val call = hackerNewsApi.getItem(id)
        return call.execute().body()!! // TODO: エラー対応
    }
}