package droidkaigi.github.io.challenge2019.data.api.response.mapper

import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.db.entity.CommentEntity
import droidkaigi.github.io.challenge2019.data.db.entity.CommentIdEntity
import droidkaigi.github.io.challenge2019.data.db.entity.StoryEntity
import java.util.*

fun Item.toStoryEntity(
    order: Int,
    alreadyRead: Boolean = false
): StoryEntity =
    StoryEntity(
        id = id,
        alreadyRead = alreadyRead,
        author = author,
        descendants = descendants,
        score = score,
        time = Date(time * 1000),
        title = title,
        url = url,
        order = order
    )

fun Item.toCommentIdEntities(): List<CommentIdEntity> =
    kids.map { commentId ->
        CommentIdEntity(
            id = commentId,
            storyId = this.id
        )
    }

fun Item.toCommentEntity(
    storyId: Long
): CommentEntity =
    CommentEntity(
        id = id,
        storyId = storyId,
        author = author,
        text = text ?: "",
        time = Date(time * 1000)
    )
