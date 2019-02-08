package droidkaigi.github.io.challenge2019.data.repository

import droidkaigi.github.io.challenge2019.data.model.*
import droidkaigi.github.io.challenge2019.infrastructure.database.PreferencesProvider
import droidkaigi.github.io.challenge2019.infrastructure.network.HackerNewsApi
import javax.inject.Inject

class HackerNewsRepositorySource @Inject constructor(
    private val hackerNewsApi: HackerNewsApi,
    private val preferencesProvider: PreferencesProvider
) : HackerNewsRepository {
    override suspend fun fetchTopStories(): List<Story> {
        val response = hackerNewsApi.getTopStories().await().take(20)
        val deferredItems = response.map {
            hackerNewsApi.getItem(it)
        }
        val articleIds = preferencesProvider.getArticleIds()
        return deferredItems.map {
            val itemResponse = it.await()
            Story(
                id = StoryId(itemResponse.id),
                author = itemResponse.author?.let { author -> Author(author) },
                title = itemResponse.title,
                url = itemResponse.url,
                score = itemResponse.score,
                time = itemResponse.time,
                commentIds = itemResponse.kids.map(::CommentId),
                alreadyRead = articleIds.contains(itemResponse.id.toString())
            )
        }
    }

    override suspend fun fetchStoryById(storyId: StoryId): Story {
        val articleIds = preferencesProvider.getArticleIds()
        val itemResponse = hackerNewsApi.getItem(storyId.v).await()
        return Story(
            id = StoryId(itemResponse.id),
            author = itemResponse.author?.let { author -> Author(author) },
            title = itemResponse.title,
            url = itemResponse.url,
            score = itemResponse.score,
            time = itemResponse.time,
            commentIds = itemResponse.kids.map(::CommentId),
            alreadyRead = articleIds.contains(itemResponse.id.toString())
        )
    }

    override suspend fun fetchCommentsByIds(commentIds: List<CommentId>): List<Comment> {
        val deferredItems = commentIds.map {
            hackerNewsApi.getItem(it.v)
        }
        return deferredItems.map {
            val itemResponse = it.await()
            Comment(
                id = CommentId(itemResponse.id),
                author = itemResponse.author?.let { author -> Author(author) },
                storyId = StoryId(itemResponse.parent),
                text = itemResponse.text,
                time = itemResponse.time
            )
        }
    }

    override suspend fun updateReadStatus(storyId: StoryId, alreadyRead: Boolean) {
        // TODO: 未読に戻せるようにするとか
        preferencesProvider.saveArticleIds(storyId.v.toString())
    }
}
