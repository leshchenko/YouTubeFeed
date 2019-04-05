package com.leshchenko.youtubefeed.model.network

import com.leshchenko.youtubefeed.model.network.models.PlayListResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("youtube/v3/playlistItems")
    fun loadPlaylistItems(@Query("playlistId") id: String,
                          @Query("maxResults") maxResults: Int,
                          @Query("part") part: String = "contentDetails,snippet",
                          @Query("key") apiKey: String = RetrofitService.apiKey,
                          @Query("pageToken") pageToken:String? = null):Call<PlayListResponseModel>
}