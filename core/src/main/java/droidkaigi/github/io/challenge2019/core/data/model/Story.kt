package droidkaigi.github.io.challenge2019.core.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Story(
    val id: Long,
    val title: String,
    val url: String,
    val score: Int,
    val author: String,
    val commentIds: List<Long>
) : Parcelable
