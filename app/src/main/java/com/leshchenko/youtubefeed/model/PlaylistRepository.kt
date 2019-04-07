package com.leshchenko.youtubefeed.model

import com.leshchenko.youtubefeed.model.local.models.Playlist
import com.leshchenko.youtubefeed.model.local.models.PlaylistLocalModel

interface PlaylistRepository {
    fun loadItems(playlist: Playlist): Result<PlaylistLocalModel>
    fun loadMore(playlist: Playlist, nextPageToken: String): Result<PlaylistLocalModel>?
    fun cancelAllRequests()
}