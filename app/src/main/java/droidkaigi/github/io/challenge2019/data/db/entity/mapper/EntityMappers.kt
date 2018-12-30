package droidkaigi.github.io.challenge2019.data.db.entity.mapper

import droidkaigi.github.io.challenge2019.data.db.entity.StoryEntity
import droidkaigi.github.io.challenge2019.model.Story

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
