package droidkaigi.github.io.challenge2019.data.db.entity.mapper

import droidkaigi.github.io.challenge2019.data.db.entity.CommentEntity
import droidkaigi.github.io.challenge2019.data.db.entity.StoryEntity
import droidkaigi.github.io.challenge2019.data.db.entity.StoryWithCommentsEntity
import droidkaigi.github.io.challenge2019.model.Comment
import droidkaigi.github.io.challenge2019.model.Story
import droidkaigi.github.io.challenge2019.model.StoryWithComments

fun StoryEntity.toStory(): Story =
    Story(
        author = author,
        descendants = descendants,
        id = id,
        commentIds = emptyList(), // TODO: delete
        score = score,
        time = time,
        title = title,
        url = url
    )

fun CommentEntity.toComment(
    comments: List<Comment> = emptyList()
): Comment =
    Comment(
        author = author,
        id = id,
        comments = comments,
        text = text,
        time = time
    )

fun StoryWithCommentsEntity.toStoryWithComments(): StoryWithComments =
        StoryWithComments(
            story = story!!.toStory(),
            comments = comments.map { it.toComment() }
        )
