package droidkaigi.github.io.challenge2019.data.repository

import droidkaigi.github.io.challenge2019.data.api.response.Item

interface HackerNewsRepository {
    suspend fun fetchTopStories(): List<Item>

    suspend fun fetchById(id: Long): Item

    suspend fun fetchByIds(ids: List<Long>): List<Item>
}
