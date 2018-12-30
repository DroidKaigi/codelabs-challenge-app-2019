package droidkaigi.github.io.challenge2019.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class StoryEntity(
    @PrimaryKey
    var id: Long,

    var author: String,
    var descendants: Int,
    var score: Int,
    var time: Date,
    var title: String,
    var url: String
)
