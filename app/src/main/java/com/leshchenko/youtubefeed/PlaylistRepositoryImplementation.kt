package com.leshchenko.youtubefeed

import android.accounts.NetworkErrorException
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leshchenko.youtubefeed.data.local.models.Playlist
import com.leshchenko.youtubefeed.data.network.RetrofitService
import com.leshchenko.youtubefeed.data.network.YouTubeApiService
import com.leshchenko.youtubefeed.data.Result
import com.leshchenko.youtubefeed.data.local.dao.PlaylistDao
import com.leshchenko.youtubefeed.data.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.data.local.models.PlaylistLocalModel
import com.leshchenko.youtubefeed.data.network.models.PlayListResponseModel
import kotlinx.coroutines.*
import retrofit2.Response


class PlaylistRepositoryImplementation(private val playlistDao: PlaylistDao, private val context: Context) :
    PlaylistRepository {
    companion object {
        private const val maxResults = 5
    }


    override fun loadItems(playlist: Playlist): Result<PlaylistLocalModel> {
        return if (isOnline()) {
            val response = RetrofitService.retrofit.create(YouTubeApiService::class.java)
                .loadPlaylistItems(playlist.playlistId, maxResults).execute()
            handleResponse(response, playlist)
        } else {
            val items = playlistDao.getPlaylistItems(playlist.playlistId)
            Result(PlaylistLocalModel(items = items))
        }
    }

    override fun loadMore(playlist: Playlist, nextPageToken: String): Result<PlaylistLocalModel>? {
        if (isOnline()) {
            val response = loadItems(playlist, maxResults, nextPageToken)
            return handleResponse(response, playlist)
        }
        return null
    }

    private fun handleResponse(
        response: Response<PlayListResponseModel>,
        playlist: Playlist
    ): Result<PlaylistLocalModel> {
        var result: Result<PlaylistLocalModel> = Result()
        if (response.isSuccessful) {
            response.body()?.let { playListResponseModel ->
                playListResponseModel.items.forEach {
                    playlistDao.insert(it.toLocal(playlist))
                }
                result = Result(playListResponseModel.toLocal(playlist))
            }
        } else {
            result = Result(null, NetworkErrorException(response.errorBody()?.string()))
        }
        return result
    }

    private fun loadItems(
        playlist: Playlist,
        maxResults: Int,
        nextPageToken: String?
    ): Response<PlayListResponseModel> {
        return RetrofitService.retrofit.create(YouTubeApiService::class.java)
            .loadPlaylistItems(id = playlist.playlistId, maxResults = maxResults, pageToken = nextPageToken).execute()
    }

    private fun isOnline(): Boolean {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo?.isConnected == true
    }
}