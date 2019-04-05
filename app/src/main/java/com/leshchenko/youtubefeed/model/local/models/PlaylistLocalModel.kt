package com.leshchenko.youtubefeed.model.local.models

data class PlaylistLocalModel(val nextPageToken: String? = null, val items: List<PlayListItemLocalModel>) {
}