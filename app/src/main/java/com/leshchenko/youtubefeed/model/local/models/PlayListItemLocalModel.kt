package com.leshchenko.youtubefeed.model.local.models

import androidx.room.*

import java.util.*
enum class Playlist(val playlistId: String) {
    FIRST("RDMMTwsvnlZNuQw"),
    SECOND("PLBCF2DAC6FFB574DE"),
    THIRD("PLBCF2DAC6FFB574DE")
}
@Entity(tableName = "playlistItems")
data class PlayListItemLocalModel(
    @ColumnInfo(name = "playlistId", index = true) var playlistId: String,
    @PrimaryKey(autoGenerate = false) var videoId: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "published_date") var publishedAt: Date,
    @ColumnInfo(name = "thumbnail_url") var thumbnailUrl: String?
) {
    constructor():this("","","","", Date(),null)
}