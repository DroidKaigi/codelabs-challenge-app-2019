package droidkaigi.github.io.challenge2019.data.repository.entity

import java.util.*

data class Story(
    val author: String,
    val descendants: Int,
    val id: Long,
    val comments: List<Comment>,
    val score: Int,
    val time: Date,
    val title: String,
    val url: String
)