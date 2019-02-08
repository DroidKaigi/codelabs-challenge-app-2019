package droidkaigi.github.io.challenge2019.core.data.repository

import droidkaigi.github.io.challenge2019.core.data.model.Comment
import droidkaigi.github.io.challenge2019.core.data.model.Story

interface HackerNewsRepository {

    suspend fun getTopStories(): List<Story>

    suspend fun getComments(story: Story): List<Comment>

    fun saveReadStoryId(id: Long)

    fun getReadStoryIds(): Set<Long>
}