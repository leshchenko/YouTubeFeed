package com.leshchenko.youtubefeed.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leshchenko.youtubefeed.model.PlaylistRepositoryImplementation

class PaginationScrollListener(private val loadMore:()-> Unit): RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
        val firstVisibleItemPosition =
            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
        if (firstVisibleItemPosition >= totalItemCount - PlaylistRepositoryImplementation.maxResults) {
            loadMore.invoke()
        }
    }
}