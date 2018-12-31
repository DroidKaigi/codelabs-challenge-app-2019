package droidkaigi.github.io.challenge2019.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import droidkaigi.github.io.challenge2019.data.db.entity.CommentIdEntity

@Dao
abstract class CommentIdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(commentIds: List<CommentIdEntity>)

    @Query("SELECT * FROM comment_id WHERE story_id = :storyId")
    abstract fun byStoryId(storyId: Long): List<CommentIdEntity>
}