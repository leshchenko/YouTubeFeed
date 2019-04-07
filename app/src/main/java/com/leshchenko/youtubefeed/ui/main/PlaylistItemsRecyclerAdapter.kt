package com.leshchenko.youtubefeed.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.leshchenko.youtubefeed.R
import com.leshchenko.youtubefeed.model.local.models.PlayListItemLocalModel
import java.text.SimpleDateFormat
import java.util.*



class PlaylistItemsRecyclerAdapter :
    ListAdapter<PlayListItemLocalModel, PlaylistItemViewHolder>(PlaylistItemsDiffUtil()) {
    private var itemClickListener: (PlayListItemLocalModel) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistItemViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistItemViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PlaylistItemViewHolder, position: Int) {
        Log.d("zlo", "bind $position")
        val item = getItem(position)
        with(holder) {
            parentView.setOnClickListener { itemClickListener.invoke(item) }
            Glide.with(holder.thumbnailImageView).load(item.thumbnailUrl).into(holder.thumbnailImageView)
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            dateTextView.text = SimpleDateFormat("E MMM yy HH:mm:ss", Locale.getDefault()).format(item.publishedAt)
        }
    }

    fun setItemClickListener(listener: (PlayListItemLocalModel) -> Unit) {
        itemClickListener = listener
    }
}

class PlaylistItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val parentView: ConstraintLayout = view.findViewById(R.id.playlistItemParentView)
    val thumbnailImageView: ImageView = view.findViewById(R.id.thumbnailImage)
    val titleTextView: TextView = view.findViewById(R.id.titleTextView)
    val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
    val dateTextView: TextView = view.findViewById(R.id.dateTextView)
}

class PlaylistItemsDiffUtil: DiffUtil.ItemCallback<PlayListItemLocalModel>() {
    override fun areItemsTheSame(oldItem: PlayListItemLocalModel, newItem: PlayListItemLocalModel): Boolean {
        return oldItem.videoId == newItem.videoId
    }

    override fun areContentsTheSame(oldItem: PlayListItemLocalModel, newItem: PlayListItemLocalModel): Boolean {
        return oldItem.description == newItem.description &&
                oldItem.playlistId == newItem.playlistId &&
                oldItem.publishedAt == newItem.publishedAt &&
                oldItem.thumbnailUrl == newItem.thumbnailUrl &&
                oldItem.title == newItem.title
    }
}