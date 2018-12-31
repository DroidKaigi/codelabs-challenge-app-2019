package droidkaigi.github.io.challenge2019.data.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

data class StoryWithCommentsEntity(
    @Embedded
    var story: StoryEntity? = null,

    @Relation(
        entity = CommentEntity::class,
        parentColumn = "id",
        entityColumn = "story_id"
    )
    var comments: List<CommentEntity> = emptyList()
)