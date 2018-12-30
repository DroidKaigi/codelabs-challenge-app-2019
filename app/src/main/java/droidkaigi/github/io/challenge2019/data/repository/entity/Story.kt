package droidkaigi.github.io.challenge2019.data.repository.entity

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Story(
    val author: String,
    val descendants: Int,
    val id: Long,
    val commentIds: List<Long>,
    val score: Int,
    val time: Date,
    val title: String,
    val url: String
)