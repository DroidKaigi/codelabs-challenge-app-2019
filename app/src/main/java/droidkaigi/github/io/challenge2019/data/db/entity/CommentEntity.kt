package droidkaigi.github.io.challenge2019.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("story_id")
        )
    ]
)
data class CommentEntity(
    @PrimaryKey
    var id: Long,

    @ColumnInfo(name = "story_id")
    var storyId: Long,

    var author: String,
    var text: String,
    var time: Date
)