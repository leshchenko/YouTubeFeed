package com.leshchenko.youtubefeed.data.network.models

import com.leshchenko.youtubefeed.data.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.data.local.models.Playlist
import java.util.*

data class PlayListResponseModel(
    val nextPageToken: String,
    val pageInfo: PageInfo,
    val items: List<PlayListItemModel>
) {
    fun toLocal(type: Playlist): List<PlayListItemLocalModel> {
        return items.map {
            PlayListItemLocalModel(
                type.playlistId,
                it.contentDetails.videoId,
                it.snippet.title,
                it.snippet.description,
                it.snippet.publishedAt,
                it.snippet.thumbnails?.default?.url
            )
        }
    }
}

data class PageInfo(val totalResults: Int)
data class PlayListItemModel(val id: String, val contentDetails: ContentDetails, val snippet: Snippet) {
    fun toLocal(type: Playlist): PlayListItemLocalModel {
        return PlayListItemLocalModel(
            type.playlistId,
            contentDetails.videoId,
            snippet.title,
            snippet.description,
            snippet.publishedAt,
            snippet.thumbnails?.default?.url
        )
    }
}

data class ContentDetails(val videoId: String)
data class Snippet(val publishedAt: Date, val title: String, val description: String, val thumbnails: Thumbnails?)
data class Thumbnails(val default: DefaultThumbnail)
data class DefaultThumbnail(val url: String)