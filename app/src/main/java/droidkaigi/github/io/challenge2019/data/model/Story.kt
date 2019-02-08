package droidkaigi.github.io.challenge2019.data.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class Story(
    val id: StoryId,
    val author: Author?,
    val title: String,
    val url: String,
    val score: Int = 0,
    val time: Long,
    val commentIds: List<CommentId>,
    val alreadyRead: Boolean = false
) : Serializable {
    companion object {
        // TODO: Localeは変えられるようにする
        private val df = SimpleDateFormat("yyyy/M/d H:MM", Locale.JAPAN)
    }

    val timeText: String = df.format(Date(time * 1000))
}
