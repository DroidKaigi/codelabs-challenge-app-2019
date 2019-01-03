package droidkaigi.github.io.challenge2019.data.repository.mapper

import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story

// TODO: 2.Implement mapper functions

fun Item.toStory(): Story = TODO("Implement this method")

fun Item.toComment(
    comments: List<Comment>
): Comment = TODO("Implement this method")