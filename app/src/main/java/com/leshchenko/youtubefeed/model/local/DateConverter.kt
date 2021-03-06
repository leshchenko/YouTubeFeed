package com.leshchenko.youtubefeed.model.local

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {

    @TypeConverter
    fun dateFromString(date: String): Date {
        return SimpleDateFormat("E MMM yy HH:mm:ss zzzz yyyy", Locale.US).parse(date)
    }

    @TypeConverter
    fun dateToString(date:Date):String {
        return date.toString()
    }
}