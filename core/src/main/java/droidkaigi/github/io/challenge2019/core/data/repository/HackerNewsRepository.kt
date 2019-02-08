package droidkaigi.github.io.challenge2019.core.data.repository

import droidkaigi.github.io.challenge2019.core.data.api.response.Item

interface HackerNewsRepository {

    suspend fun getTopStories(): List<Item>

    suspend fun getComments(item: Item): List<Item>
}