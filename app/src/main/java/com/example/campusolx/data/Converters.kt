package com.example.campusolx.data

import androidx.room.TypeConverter
import java.sql.Time

class Converters {
    @TypeConverter
    fun fromTime(time: Time?): Long? {
        return time?.time
    }

    @TypeConverter
    fun toTime(timestamp: Long?): Time? {
        return timestamp?.let { Time(it) }
    }
}
