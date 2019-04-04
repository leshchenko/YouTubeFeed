package com.leshchenko.youtubefeed.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.leshchenko.youtubefeed.PlaylistRepositoryImplementation
import com.leshchenko.youtubefeed.data.local.models.Playlist
import com.leshchenko.youtubefeed.data.Result
import com.leshchenko.youtubefeed.data.local.PlaylistDatabase
import com.leshchenko.youtubefeed.data.local.models.PlayListItemLocalModel

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository by lazy {
        PlaylistRepositoryImplementation(PlaylistDatabase.getDatabase(application).playlistDao(), application)
    }

    fun loadPlaylist(playlist: Playlist): LiveData<Result<List<PlayListItemLocalModel>>> {
        return repository.loadItems(playlist)
    }
}