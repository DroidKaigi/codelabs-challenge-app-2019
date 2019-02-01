package droidkaigi.github.io.challenge2019.data.repository.mapper

import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story
import java.util.*

// TODO: 2.Implement mapper functions

fun Item.toStory(): Story = TODO("Implement this method")

fun Item.toComment(
    comments: List<Comment>
): Comment = Comment(
    author = author,
    id = id,
    comments = comments,
    text = text ?: "",
    time = Date(time * 1000)
)