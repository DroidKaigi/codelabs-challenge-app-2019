package droidkaigi.github.io.challenge2019.data.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import droidkaigi.github.io.challenge2019.data.db.entity.StoryWithCommentsEntity

@Dao
abstract class StoryCommentJoinDao {

    @Transaction
    @Query("SELECT * FROM story WHERE id = :storyId")
    abstract fun byStoryIdWithComments(storyId: Long): LiveData<StoryWithCommentsEntity?>
}
