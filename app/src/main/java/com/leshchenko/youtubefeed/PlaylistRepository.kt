package com.leshchenko.youtubefeed

import androidx.lifecycle.LiveData
import com.leshchenko.youtubefeed.data.Result
import com.leshchenko.youtubefeed.data.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.data.local.models.Playlist

interface PlaylistRepository {
    fun loadItems(playlist: Playlist): LiveData<Result<List<PlayListItemLocalModel>>>
}