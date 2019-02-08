package droidkaigi.github.io.challenge2019.core.data.repository

import droidkaigi.github.io.challenge2019.core.data.api.response.Item
import droidkaigi.github.io.challenge2019.core.data.model.Story

interface HackerNewsRepository {

    suspend fun getTopStories(): List<Story>

    suspend fun getComments(story: Story): List<Item>
}