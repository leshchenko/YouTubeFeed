package com.leshchenko.youtubefeed.model

import android.accounts.NetworkErrorException
import android.content.Context
import com.leshchenko.youtubefeed.model.local.dao.PlaylistDao
import com.leshchenko.youtubefeed.model.local.models.Playlist
import com.leshchenko.youtubefeed.model.local.models.PlaylistLocalModel
import com.leshchenko.youtubefeed.model.network.RetrofitService
import com.leshchenko.youtubefeed.model.network.YouTubeApiService
import com.leshchenko.youtubefeed.model.network.models.PlayListResponseModel
import com.leshchenko.youtubefeed.util.isOnline
import retrofit2.Call
import retrofit2.Response


class PlaylistRepositoryImplementation(private val playlistDao: PlaylistDao, private val context: Context) :
    PlaylistRepository {
    companion object {
        private const val maxResults = 10
    }

    private var currentCall: Call<PlayListResponseModel>? = null
    override fun loadItems(playlist: Playlist): Result<PlaylistLocalModel> {
        var result = Result<PlaylistLocalModel>()
         if (isOnline(context)) {
            currentCall = RetrofitService.retrofit.create(YouTubeApiService::class.java)
                .loadPlaylistItems(playlist.playlistId,
                    maxResults
                )
            currentCall?.execute()?.let { result = handleResponse(it, playlist) }
        } else {
            val items = playlistDao.getPlaylistItems(playlist.playlistId)
            result = Result(PlaylistLocalModel(items = items))
        }

        return result
    }

    override fun loadMore(playlist: Playlist, nextPageToken: String): Result<PlaylistLocalModel>? {
        var result = Result<PlaylistLocalModel>()
        if (isOnline(context)) {
            val response = loadItems(playlist,
                maxResults, nextPageToken)
            response?.let {
                result = handleResponse(it, playlist)
            }
        }
        return result
    }

    override fun cancelAllRequests() {
        currentCall?.cancel()
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
    ): Response<PlayListResponseModel>? {
        currentCall = RetrofitService.retrofit.create(YouTubeApiService::class.java)
            .loadPlaylistItems(id = playlist.playlistId, maxResults = maxResults, pageToken = nextPageToken)
        return currentCall?.execute()
    }
}