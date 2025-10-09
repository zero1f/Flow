package com.zero.flow.data.local.database

import androidx.room.TypeConverter
import com.zero.flow.domain.model.SessionType
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromSessionType(value: SessionType): String {
        return value.name
    }

    @TypeConverter
    fun toSessionType(value: String): SessionType {
        return SessionType.valueOf(value)
    }
}