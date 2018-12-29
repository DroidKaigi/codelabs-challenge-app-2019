package droidkaigi.github.io.challenge2019.data.repository.entity

import java.util.*

data class Comment(
    val author: String,
    val id: Long,
    val comments: List<Comment>,
    val text: String,
    val time: Date
)
