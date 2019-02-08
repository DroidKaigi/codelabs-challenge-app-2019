package droidkaigi.github.io.challenge2019.data.model

import java.io.Serializable

data class Comment(
    val id: CommentId,
    val author: Author?,
    val storyId: StoryId,
    val text: String?,
    val time: Long
) : Serializable
