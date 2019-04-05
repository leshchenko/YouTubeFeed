package com.leshchenko.youtubefeed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leshchenko.youtubefeed.data.local.models.PlayListItemLocalModel

@Dao
interface PlaylistDao {

    @Query("SELECT * from playlistItems WHERE playlistId = :playlistId")
    fun getPlaylistItems(playlistId:String): List<PlayListItemLocalModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlistItem: PlayListItemLocalModel)
}