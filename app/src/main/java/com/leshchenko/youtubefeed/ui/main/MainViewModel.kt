package com.leshchenko.youtubefeed.ui.main

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.leshchenko.youtubefeed.R
import com.leshchenko.youtubefeed.model.PlaylistRepositoryImplementation
import com.leshchenko.youtubefeed.model.Result
import com.leshchenko.youtubefeed.model.local.PlaylistDatabase
import com.leshchenko.youtubefeed.model.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.model.local.models.Playlist
import com.leshchenko.youtubefeed.model.local.models.PlaylistLocalModel
import com.leshchenko.youtubefeed.ui.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : BaseViewModel(application) {

    val playlistItems: MutableLiveData<List<PlayListItemLocalModel>> = MutableLiveData()
    val displayError = MutableLiveData<Pair<Boolean, String>>()

    var currentPlaylist: Playlist = Playlist.FIRST
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

        if (playlist == null || currentPlaylist.playlistId != playlist.playlistId) {
            currentPlaylist = playlist ?: Playlist.FIRST
            reloadCurrentPlaylist()
        }
    }

    fun reloadCurrentPlaylist() {
        loadedItems.clear()
        isLoading = true
        launch {
            val result = repository.loadItems(currentPlaylist)
            handleResult(result)
        }
    }

    fun loadMoreItems() {
        if (isLoading) {
            return
        }
        nextPageToken?.let {
            isLoading = true
            launch {
                repository.loadMore(currentPlaylist, it)?.let { handleResult(it) }
            }
        }
    }

    private fun handleResult(result: Result<PlaylistLocalModel>) {
        result.withResult(
            success = {
                handleSuccess(it)
            },
            error = {
                Log.e(MainViewModel::class.java.simpleName, it.localizedMessage)
                isLoading = false
                nextPageToken = null
                displayError.postValue(Pair(true, getString(R.string.error_happened)))
            })
    }

    private fun handleSuccess(model: PlaylistLocalModel) {
        displayError.postValue(Pair(false, ""))
        isLoading = false
        loadedItems.addAll(model.items)
        nextPageToken = model.nextPageToken

        if (loadedItems.isEmpty()) {
            displayError.postValue(Pair(true, getString(R.string.no_items)))
        } else {
            val newList = mutableListOf<PlayListItemLocalModel>()
            newList.addAll(loadedItems)
            playlistItems.postValue(newList)
        }
    }

    private fun getString(@StringRes resId: Int): String {
        return getApplication<Application>().getString(resId)
    }
}