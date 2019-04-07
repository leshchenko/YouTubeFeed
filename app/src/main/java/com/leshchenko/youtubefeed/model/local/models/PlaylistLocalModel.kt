package com.leshchenko.youtubefeed.model.local.models

enum class Playlist(val playlistId: String) {
    FIRST("PL4o29bINVT4EG_y-k5jGoOu3-Am8Nvi10"),
    SECOND("PLE6rhv8iI_vJ48CbdqZqnr-6LT4A_Lxtq"),
    THIRD("PLdKFtHTuSEuN3va3L9yj8bkJYTQfrMSIx")
}

data class PlaylistLocalModel(val nextPageToken: String? = null, val items: List<PlayListItemLocalModel>) {
}