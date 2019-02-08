package droidkaigi.github.io.challenge2019.infrastructure.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class ItemResponse(
    @Json(name = "id")
    val id: Long = NO_ID,
    @Json(name = "deleted")
    val deleted: Boolean = false,
    @Json(name = "type")
    val type: String = "",
    @Json(name = "by")
    val author: String? = null,
    @Json(name = "time")
    val time: Long = 0L,
    @Json(name = "text")
    val text: String? = "",
    @Json(name = "dead")
    val dead: Boolean = false,
    @Json(name = "parent")
    val parent: Long = NO_ID,
    @Json(name = "poll")
    val poll: Long = NO_ID,
    @Json(name = "kids")
    val kids: List<Long> = emptyList(),
    @Json(name = "url")
    val url: String = "",
    @Json(name = "score")
    val score: Int = 0,
    @Json(name = "title")
    val title: String = "",
    @Json(name = "parts")
    val parts: List<Long> = emptyList(),
    @Json(name = "descendants")
    val descendants: Int = 0
) : Serializable {
    companion object {
        const val NO_ID = -1L

        // TODO: Localeは変えられるようにする
        private val df = SimpleDateFormat("yyyy/M/d H:MM", Locale.JAPAN)
    }

    val timeText: String = df.format(Date(time * 1000))
}