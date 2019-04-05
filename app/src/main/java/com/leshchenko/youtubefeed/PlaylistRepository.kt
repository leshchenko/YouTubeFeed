package com.leshchenko.youtubefeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leshchenko.youtubefeed.data.Result
import com.leshchenko.youtubefeed.data.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.data.local.models.Playlist
import com.leshchenko.youtubefeed.data.local.models.PlaylistLocalModel
import com.leshchenko.youtubefeed.data.network.models.PlayListResponseModel
import retrofit2.Response

interface PlaylistRepository {
    fun loadItems(playlist: Playlist): Result<PlaylistLocalModel>
    fun loadMore(playlist: Playlist, nextPageToken: String): Result<PlaylistLocalModel>?
}