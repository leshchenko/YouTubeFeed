package com.leshchenko.youtubefeed.data.local.models

data class PlaylistLocalModel(val nextPageToken: String? = null, val items: List<PlayListItemLocalModel>) {
}