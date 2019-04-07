package com.leshchenko.youtubefeed.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.leshchenko.youtubefeed.model.PlaylistRepositoryImplementation
import com.leshchenko.youtubefeed.model.local.models.Playlist
import com.leshchenko.youtubefeed.model.Result
import com.leshchenko.youtubefeed.model.local.PlaylistDatabase
import com.leshchenko.youtubefeed.model.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.model.local.models.PlaylistLocalModel
import com.leshchenko.youtubefeed.util.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val playlistItems: MutableLiveData<List<PlayListItemLocalModel>> = MutableLiveData()
    val displayError = MutableLiveData<Boolean>()

    var currentPlaylist: Playlist? = null
    private var nextPageToken: String? = null
    private var loadedItems = mutableListOf<PlayListItemLocalModel>()
    private var isLoading = false

    private val repository by lazy {
        PlaylistRepositoryImplementation(
            PlaylistDatabase.getDatabase(application).playlistDao(),
            application
        )
    }

    fun loadPlaylist(playlist: Playlist?) {
        if (isLoading) {
            repository.cancelAllRequests()
        }
        if (playlist == null) {
            currentPlaylist = Playlist.FIRST
        }

        if (currentPlaylist?.playlistId != playlist?.playlistId) {
            loadedItems.clear()
            currentPlaylist = playlist ?: Playlist.FIRST
            isLoading = true
            CoroutineScope(Dispatchers.IO).launch {
                val result = repository.loadItems(playlist ?: currentPlaylist ?: Playlist.FIRST)
                handleResult(result)
            }
        }

    }

    fun loadMoreItems() {
        if (isLoading) {
            return
        }
        val playlist = currentPlaylist
        val token = nextPageToken
        if (playlist != null && token != null) {
            isLoading = true
            CoroutineScope(Dispatchers.IO).launch {
                repository.loadMore(playlist, token)?.let { handleResult(it) }
            }
        }
    }

    private fun handleResult(result: Result<PlaylistLocalModel>) {
        result.withResult(
            success = {
                displayError.postValue(false)
                loadedItems.addAll(it.items)
                nextPageToken = it.nextPageToken
                playlistItems.postValue(loadedItems)
                isLoading = false
            },
            error = {
                Log.e(MainViewModel::class.java.simpleName, it.localizedMessage)
                isLoading = false
                displayError.postValue(true)
            })
    }
}