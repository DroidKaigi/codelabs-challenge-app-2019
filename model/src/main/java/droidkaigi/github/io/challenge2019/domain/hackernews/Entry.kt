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

sealed class Entry {
    abstract val id: EntryId
    abstract val time: Date
    abstract val by: String
}

data class Story(
    override val id: EntryId,
    override val time: Date,
    override val by: String,
    val descendants: Int,
    val kids: List<EntryId> = emptyList(),
    val score: Int,
    val title: String,
    val url: URL
) : Entry()

data class Comment(
    override val id: EntryId,
    override val time: Date,
    override val by: String,
    val kids: List<EntryId> = emptyList(),
    val parent: EntryId,
    val htmlText: String
) : Entry()

data class Ask(
    override val id: EntryId,
    override val by: String,
    override val time: Date,
    val kids: List<EntryId> = emptyList(),
    val score: Int,
    val htmlText: String,
    val title: String,
    val descendants: Int
) : Entry()

data class Job(
    override val id: EntryId,
    override val by: String,
    override val time: Date,
    val score: Int,
    val htmlText: String,
    val title: String
) : Entry()

data class Poll(
    override val id: EntryId,
    override val time: Date,
    override val by: String,
    val kids: List<EntryId> = emptyList(),
    val parts: List<EntryId> = emptyList(),
    val score: Int,
    val htmlText: String? = null,
    val title: String,
    val descendants: Int
) : Entry()

data class Pollopt(
    override val by: String,
    override val id: EntryId,
    override val time: Date,
    val poll: EntryId,
    val score: Int,
    val htmlText: String
) : Entry()