package droidkaigi.github.io.challenge2019.util

import droidkaigi.github.io.challenge2019.data.api.response.ItemResponse
import droidkaigi.github.io.challenge2019.domain.hackernews.*
import java.net.MalformedURLException
import java.net.URL
import java.util.*

private fun Long.toEntryId(): EntryId = EntryId(this)

private fun String.emptyToNullable(): String? = if (this.isEmpty())
    null
else
    this

private fun String.toURL(): URL? = try {
    URL(this)
} catch (_: MalformedURLException) {
    null
}


fun ItemResponse.toEntry(): Entry = when (EntryType.valueOf(this.type.toUpperCase())) {
    EntryType.JOB -> Job(
        id = id.toEntryId(),
        by = author,
        time = Date(time * 1000L),
        score = score,
        htmlText = text!!,
        title = title
    )
    EntryType.STORY -> {
        val url = url.toURL()
        if (url != null) {
            Story(
                by = author,
                descendants = descendants,
                id = id.toEntryId(),
                kids = kids.map(::EntryId),
                score = score,
                time = Date(time * 1000L),
                title = title,
                url = url
            )
        } else {
            Ask(
                by = author,
                descendants = descendants,
                id = id.toEntryId(),
                kids = kids.map(::EntryId),
                score = score,
                htmlText = text!!,
                time = Date(time * 1000L),
                title = title
            )
        }
    }
    EntryType.COMMENT -> Comment(
        id = id.toEntryId(),
        by = author,
        kids = kids.map(::EntryId),
        parent = parent.toEntryId(),
        htmlText = text!!,
        time = Date(time * 1000L)
    )
    EntryType.POLL -> Poll(
        id = id.toEntryId(),
        by = author,
        descendants = descendants,
        kids = kids.map(::EntryId),
        parts = parts.map(::EntryId),
        score = score,
        htmlText = text?.emptyToNullable(),
        time = Date(time * 1000L),
        title = title
    )
    EntryType.POLLOPT -> Pollopt(
        by = author,
        id = id.toEntryId(),
        poll = poll.toEntryId(),
        score = score,
        htmlText = text!!,
        time = Date(time * 1000L)
    )
}