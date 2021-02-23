package com.dhbw.cas.integra

import androidx.room.TypeConverter
import java.sql.Time

class Converters {
    @TypeConverter
    fun timestampToTime(value: Long?): Time? {
        return value?.let { Time(it) }
    }

    @TypeConverter
    fun timeToTimestamp(time: Time?): Long? {
        return time?.time
    }
}