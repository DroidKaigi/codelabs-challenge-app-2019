package droidkaigi.github.io.challenge2019.data.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import droidkaigi.github.io.challenge2019.data.db.entity.StoryEntity

@Dao
abstract class StoryDao {

    @Query("SELECT * FROM story ORDER BY story.`order`")
    abstract fun getAllStories(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM story WHERE already_read = 1")
    abstract fun getAlreadyReadStories(): List<StoryEntity>

    @Query("DELETE FROM story")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(stories: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(story: StoryEntity)

    @Query("SELECT * FROM story WHERE id = :id")
    abstract fun byIdAsLive(id: Long): LiveData<StoryEntity?>

    @Query("SELECT * FROM story WHERE id = :id")
    abstract fun byId(id: Long): StoryEntity?

    @Update
    abstract fun update(story: StoryEntity)

    @Transaction
    open fun clearAndInsert(newStories: List<StoryEntity>) {
        deleteAll()
        insert(newStories)
    }
}
