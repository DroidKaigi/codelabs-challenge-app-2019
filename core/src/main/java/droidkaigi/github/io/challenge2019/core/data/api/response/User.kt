package droidkaigi.github.io.challenge2019.core.data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id")
    val id: String,
    @Json(name = "delay")
    val delay: Long,
    @Json(name = "created")
    val created: Long,
    @Json(name = "karma")
    val karma: Int,
    @Json(name = "about")
    val about: String,
    @Json(name = "submitted")
    val submitted: List<Long>
)