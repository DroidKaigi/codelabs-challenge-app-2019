package droidkaigi.github.io.challenge2019.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import droidkaigi.github.io.challenge2019.data.db.entity.CommentEntity

@Dao
abstract class CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(comments: List<CommentEntity>)

}