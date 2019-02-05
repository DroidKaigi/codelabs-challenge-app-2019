package droidkaigi.github.io.challenge2019.data.repository.mapper

import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story
import java.util.*

fun Item.toStory(): Story = Story(
    author = author,
    descendants = descendants,
    id = id,
    commentIds = kids,
    score = score,
    time = Date(time * 1000),
    title = title,
    url = url
)

fun Item.toComment(
    comments: List<Comment>
): Comment = Comment(
    author = author,
    id = id,
    comments = comments,
    text = text ?: "",
    time = Date(time * 1000)
)
