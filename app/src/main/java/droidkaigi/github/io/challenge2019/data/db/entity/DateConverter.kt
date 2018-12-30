package droidkaigi.github.io.challenge2019.data.db.entity

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun fromLong(value: Long?) = value?.let { Date(it) }

    @TypeConverter
    fun toLong(date: Date?) = date?.time
}