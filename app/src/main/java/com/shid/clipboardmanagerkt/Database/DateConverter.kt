package com.shid.clipboardmanagerkt.Database

import androidx.room.TypeConverter
import java.util.*

object DateConverter {


    /*@TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Calendar? = value?.let { value ->
        GregorianCalendar().also { calendar ->
            calendar.timeInMillis = value
        }
    }

    @TypeConverter
    @JvmStatic
    fun toTimestamp(timestamp: Calendar?): Long? = timestamp?.timeInMillis*/

    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    @JvmStatic
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}