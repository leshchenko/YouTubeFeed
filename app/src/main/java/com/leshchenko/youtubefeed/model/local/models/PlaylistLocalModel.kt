package com.leshchenko.youtubefeed.model.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

enum class Playlist(val playlistId: String) {
    FIRST("PL4o29bINVT4EG_y-k5jGoOu3-Am8Nvi10"),
    SECOND("PLE6rhv8iI_vJ48CbdqZqnr-6LT4A_Lxtq"),
    THIRD("PLdKFtHTuSEuN3va3L9yj8bkJYTQfrMSIx")
}

data class PlaylistLocalModel(val nextPageToken: String? = null, val items: List<PlayListItemLocalModel>)

@Entity(tableName = "playlistItems")
data class PlayListItemLocalModel(
    @ColumnInfo(name = "playlistId", index = true) var playlistId: String = "",
    @PrimaryKey(autoGenerate = false) var videoId: String = "",
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "published_date") var publishedAt: Date = Date(),
    @ColumnInfo(name = "thumbnail_url") var thumbnailUrl: String? = ""
)