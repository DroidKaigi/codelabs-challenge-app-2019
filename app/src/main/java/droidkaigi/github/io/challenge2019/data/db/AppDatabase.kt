package droidkaigi.github.io.challenge2019.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import droidkaigi.github.io.challenge2019.data.db.dao.CommentDao
import droidkaigi.github.io.challenge2019.data.db.dao.CommentIdDao
import droidkaigi.github.io.challenge2019.data.db.dao.StoryCommentJoinDao
import droidkaigi.github.io.challenge2019.data.db.dao.StoryDao
import droidkaigi.github.io.challenge2019.data.db.entity.CommentEntity
import droidkaigi.github.io.challenge2019.data.db.entity.CommentIdEntity
import droidkaigi.github.io.challenge2019.data.db.entity.DateConverter
import droidkaigi.github.io.challenge2019.data.db.entity.StoryEntity

@Database(
    entities = [
        StoryEntity::class,
        CommentIdEntity::class,
        CommentEntity::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun commentIdDao(): CommentIdDao
    abstract fun commentDao(): CommentDao
    abstract fun storyCommentJoinDao(): StoryCommentJoinDao
}
