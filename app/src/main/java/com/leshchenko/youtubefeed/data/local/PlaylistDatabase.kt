package com.leshchenko.youtubefeed.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.leshchenko.youtubefeed.data.local.dao.PlaylistDao
import com.leshchenko.youtubefeed.data.local.models.PlayListItemLocalModel

@Database(entities = [PlayListItemLocalModel::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
public abstract class PlaylistDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: PlaylistDatabase? = null

        fun getDatabase(context: Context): PlaylistDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context, PlaylistDatabase::class.java, "Playlist_database").build()
                INSTANCE = instance
                instance
            }
        }
    }
}