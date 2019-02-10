package droidkaigi.github.io.challenge2019.domain.hackernews

import java.net.URL
import java.util.*

data class EntryId(val id: Long)

enum class EntryType {
    JOB,
    STORY,
    COMMENT,
    POLL,
    POLLOPT
}

data class Entry(
    val id: EntryId,
    val type: EntryType,
    val time: Date,
    val author: String,
    val htmlText: String? = null,
    val parent: EntryId? = null,
    val poll: EntryId? = null,
    val kids: List<EntryId> = emptyList(),
    val url: URL? = null,
    val score: Int,
    val title: String,
    val parts: List<EntryId> = emptyList(),
    val descendants: Int
)