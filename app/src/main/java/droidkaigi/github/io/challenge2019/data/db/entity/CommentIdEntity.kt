package droidkaigi.github.io.challenge2019.data.db.entity

import android.arch.persistence.room.*

@Entity(
    tableName = "comment_id",
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("story_id"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["story_id"])
    ]
)
data class CommentIdEntity(
    @PrimaryKey
    var id: Long,

    @ColumnInfo(name = "story_id")
    var storyId: Long
)