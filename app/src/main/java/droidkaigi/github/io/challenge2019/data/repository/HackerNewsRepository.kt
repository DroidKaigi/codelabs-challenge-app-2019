package droidkaigi.github.io.challenge2019.data.repository

import droidkaigi.github.io.challenge2019.data.model.Comment
import droidkaigi.github.io.challenge2019.data.model.CommentId
import droidkaigi.github.io.challenge2019.data.model.Story
import droidkaigi.github.io.challenge2019.data.model.StoryId

interface HackerNewsRepository {
    suspend fun fetchTopStories(): List<Story>

    suspend fun fetchStoryById(storyId: StoryId): Story

    suspend fun fetchCommentsByIds(commentIds: List<CommentId>): List<Comment>

    suspend fun updateReadStatus(storyId: StoryId, alreadyRead: Boolean)
}
