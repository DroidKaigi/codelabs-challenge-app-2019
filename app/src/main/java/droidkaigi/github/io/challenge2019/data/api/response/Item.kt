package droidkaigi.github.io.challenge2019.data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "id")
    val id: Long,
    @Json(name = "deleted")
    val deleted: Boolean = false,
    @Json(name = "type")
    val type: String,
    @Json(name = "by")
    val author: String,
    @Json(name = "time")
    val time: Long,
    @Json(name = "text")
    val text: String = "",
    @Json(name = "dead")
    val dead: Boolean = false,
    @Json(name = "parent")
    val parent: Long = NO_ID,
    @Json(name = "poll")
    val poll: Long = NO_ID,
    @Json(name = "kids")
    val kids: List<Long> = emptyList(),
    @Json(name = "url")
    val url: String,
    @Json(name = "score")
    val score: Int = 0,
    @Json(name = "title")
    val title: String,
    @Json(name = "parts")
    val parts: List<Long> = emptyList(),
    @Json(name = "descendants")
    val descendants: Int = 0
) {
    companion object {
        const val NO_ID = -1L
    }
}