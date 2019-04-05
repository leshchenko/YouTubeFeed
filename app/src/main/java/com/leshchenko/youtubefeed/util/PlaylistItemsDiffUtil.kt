package com.leshchenko.youtubefeed.util

import androidx.recyclerview.widget.DiffUtil
import com.leshchenko.youtubefeed.model.local.models.PlayListItemLocalModel

class PlaylistItemsDiffUtil(val oldList: List<PlayListItemLocalModel>, val newList: List<PlayListItemLocalModel>): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].videoId == newList[newItemPosition].videoId
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.description == newItem.description &&
                oldItem.playlistId == newItem.playlistId &&
                oldItem.publishedAt == newItem.publishedAt &&
                oldItem.thumbnailUrl == newItem.thumbnailUrl &&
                oldItem.title == newItem.title

    }
}