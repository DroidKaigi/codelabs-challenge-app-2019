package droidkaigi.github.io.challenge2019.data.repository

import droidkaigi.github.io.challenge2019.data.model.Article

interface HackerNewsRepository {
    suspend fun fetchTopStories(): List<Article>

    suspend fun fetchById(id: Long): Article

    suspend fun fetchByIds(ids: List<Long>): List<Article>

    suspend fun updateReadStatus(id: Long, alreadyRead: Boolean)
}
