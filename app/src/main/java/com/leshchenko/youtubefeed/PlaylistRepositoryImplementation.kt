package com.leshchenko.youtubefeed

import android.accounts.NetworkErrorException
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leshchenko.youtubefeed.data.local.models.Playlist
import com.leshchenko.youtubefeed.data.network.RetrofitService
import com.leshchenko.youtubefeed.data.network.YouTubeApiService
import com.leshchenko.youtubefeed.data.network.models.PlayListResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.leshchenko.youtubefeed.data.Result
import com.leshchenko.youtubefeed.data.local.PlaylistDatabase
import com.leshchenko.youtubefeed.data.local.dao.PlaylistDao
import com.leshchenko.youtubefeed.data.local.models.PlayListItemLocalModel
import kotlinx.coroutines.*


class PlaylistRepositoryImplementation(val playlistDao: PlaylistDao, val context: Context) : PlaylistRepository {
    private var storedPlaylist: MutableList<PlayListItemLocalModel>? = null

    override fun loadItems(playlist: Playlist): LiveData<Result<List<PlayListItemLocalModel>>> {

        val responseLiveData = MutableLiveData<Result<List<PlayListItemLocalModel>>>()
        if (storedPlaylist?.first()?.playlistId == playlist.playlistId) {
            Log.d("zlo", "return stored")
            responseLiveData.value = Result(storedPlaylist)
        } else {
            Log.d("zlo", "make call")

            CoroutineScope(Dispatchers.IO).launch {
                if (isOnline()) {
                    val response = RetrofitService.retrofit.create(YouTubeApiService::class.java)
                        .loadPlaylistItems(playlist.playlistId, 25).execute()
                    if (response.isSuccessful) {
                        response.body()?.let { playListResponseModel ->
//                            playlistDao.insert(playListResponseModel.toLocal(playlist))
                            responseLiveData.postValue(Result(playListResponseModel.toLocal(playlist)))
                        }
                    } else {
                        responseLiveData.postValue(Result(null, NetworkErrorException(response.errorBody()?.string())))
                    }
                } else {
                    val items = playlistDao.getPlaylistItems(playlist.playlistId)
                    Log.d("zlo", "items count = ${items.size}")
                }
            }
        }
        return responseLiveData
    }

    private fun isOnline(): Boolean {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo?.isConnected == true
    }
}