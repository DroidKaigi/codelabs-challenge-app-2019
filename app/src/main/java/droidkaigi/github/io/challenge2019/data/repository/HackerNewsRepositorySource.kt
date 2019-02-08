package droidkaigi.github.io.challenge2019.data.repository

import droidkaigi.github.io.challenge2019.data.model.*
import droidkaigi.github.io.challenge2019.infrastructure.database.PreferencesProvider
import droidkaigi.github.io.challenge2019.infrastructure.network.HackerNewsApi
import droidkaigi.github.io.challenge2019.infrastructure.network.ItemResponse
import javax.inject.Inject

class HackerNewsRepositorySource @Inject constructor(
    private val hackerNewsApi: HackerNewsApi,
    private val preferencesProvider: PreferencesProvider // TODO: Roomに移行したらDaoをDIしてDBにキャッシュ
) : HackerNewsRepository {
    override suspend fun fetchTopStories(): List<Story> {
        // TODO: 20はどこに持つかな。。。
        val response = hackerNewsApi.getTopStories().await().take(20)
        val deferredItems = response.map {
            hackerNewsApi.getItem(it)
        }
        val articleIds = preferencesProvider.getArticleIds()
        return deferredItems.map { it.await().toStory(articleIds) }
    }

    override suspend fun fetchStoryById(storyId: StoryId): Story {
        val articleIds = preferencesProvider.getArticleIds()
        val itemResponse = hackerNewsApi.getItem(storyId.v).await()
        return itemResponse.toStory(articleIds)
    }

    override suspend fun fetchCommentsByIds(commentIds: List<CommentId>): List<Comment> {
        val deferredItems = commentIds.map {
            hackerNewsApi.getItem(it.v)
        }
        return deferredItems.map {
            val itemResponse = it.await()
            itemResponse.toComment()
        }
    }

    override suspend fun updateReadStatus(storyId: StoryId, alreadyRead: Boolean) {
        // TODO: 未読に戻せるようにする？
        preferencesProvider.saveArticleIds(storyId.v.toString())
    }
}

private fun ItemResponse.toStory(articleIds: Set<String>) = Story(
    id = StoryId(id),
    author = author?.let { author -> Author(author) },
    title = title,
    url = url,
    score = score,
    time = time,
    commentIds = kids.map(::CommentId),
    alreadyRead = articleIds.contains(id.toString())
)

private fun ItemResponse.toComment() = Comment(
    id = CommentId(id),
    author = author?.let { author -> Author(author) },
    storyId = StoryId(parent),
    text = text,
    time = time
)
