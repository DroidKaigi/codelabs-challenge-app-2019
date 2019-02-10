package droidkaigi.github.io.challenge2019.util

import droidkaigi.github.io.challenge2019.data.api.response.ItemResponse
import droidkaigi.github.io.challenge2019.domain.hackernews.Entry
import droidkaigi.github.io.challenge2019.domain.hackernews.EntryId
import droidkaigi.github.io.challenge2019.domain.hackernews.EntryType
import java.net.MalformedURLException
import java.net.URL
import java.util.*

private fun Long.toEntryId(): EntryId? = if (this == ItemResponse.NO_ID)
    null
else
    EntryId(this)

private fun String.emptyToNullable(): String? = if (this.isEmpty())
    null
else
    this

private fun String.toURL(): URL? = try {
    URL(this)
} catch (_: MalformedURLException) {
    null
}


fun ItemResponse.toEntry(): Entry = Entry(
    id = id.toEntryId()!!,
    type = EntryType.valueOf(type.toUpperCase()),
    time = Date(time * 1000L),
    htmlText = text?.emptyToNullable(),
    parent = parent.toEntryId(),
    poll = poll.toEntryId(),
    kids = kids.mapNotNull { it.toEntryId() },
    url = url.toURL(),
    score = score,
    title = title,
    parts = parts.mapNotNull { it.toEntryId() },
    descendants = descendants,
    author = author
)