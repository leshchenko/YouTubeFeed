package com.leshchenko.youtubefeed.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.leshchenko.youtubefeed.R
import com.leshchenko.youtubefeed.model.PlaylistRepositoryImplementation
import com.leshchenko.youtubefeed.model.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.model.local.models.Playlist
import com.leshchenko.youtubefeed.model.network.RetrofitService
import com.leshchenko.youtubefeed.util.PlaylistItemsDiffUtil
import com.leshchenko.youtubefeed.util.isOnline
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }
    private val adapter by lazy {
        PlaylistItemsRecyclerAdapter(mutableListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupUI(savedInstanceState)
        viewModel.loadPlaylist(viewModel.currentPlaylist)
    }

    private fun setupUI(savedInstanceState: Bundle?) {
        progressBar.visibility = VISIBLE
        setupRecyclerView()
        retryButton.setOnClickListener {
            isOnline(this, makeIsOnline = {
                errorGroup.visibility = GONE
                progressBar.visibility = VISIBLE
                viewModel.reloadCurrentPlaylist()
            })
        }
        defineObservers()
        setupDrawer(savedInstanceState)
    }

    private fun setupRecyclerView() {
        adapter.setItemClickListener { playVideo(it) }
        playlistItemsRecycleView.adapter = adapter

        playlistItemsRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
                val firstVisibleItemPosition =
                    (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
                if (firstVisibleItemPosition >= totalItemCount - PlaylistRepositoryImplementation.maxResults) {
                    isOnline(this@MainActivity, makeIsOnline = {
                        viewModel.loadMoreItems()
                    }, makeIsOffline = {})
                }
            }
        })
    }

    private fun defineObservers() {
        viewModel.displayError.observe(this, Observer {
            progressBar.visibility = GONE
            errorGroup.visibility = if (it) VISIBLE else GONE
            playlistItemsRecycleView.visibility = if (it) GONE else VISIBLE
            errorTextView.text = getString(R.string.error_happened)
        })
        viewModel.playlistItems.observe(this, Observer {
            progressBar.visibility = GONE
            if (it.isEmpty()) {
                displayNoVideosError()
            } else {
                displayItems(it)
            }
        })
    }

    private fun playVideo(item: PlayListItemLocalModel) {
        isOnline(this, makeIsOnline = {
            val intent = YouTubeStandalonePlayer.createVideoIntent(this, RetrofitService.apiKey, item.videoId)
            startActivity(intent)
        })
    }

    private fun setupDrawer(savedInstanceState: Bundle?) {
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        savedInstanceState ?: let { nav_view.setCheckedItem(R.id.first_playlist) }
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun displayItems(items: List<PlayListItemLocalModel>) {
        errorGroup.visibility = GONE
        playlistItemsRecycleView.visibility = VISIBLE
        val diffCallback = PlaylistItemsDiffUtil(adapter.getData(), items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, false)
        adapter.setData(items)
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun displayNoVideosError() {
        errorGroup.visibility = VISIBLE
        playlistItemsRecycleView.visibility = GONE
        errorTextView.text = getString(R.string.no_items)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        playlistItemsRecycleView.visibility = GONE
        progressBar.visibility = VISIBLE
        errorGroup.visibility = GONE
        when (item.itemId) {
            R.id.first_playlist -> viewModel.loadPlaylist(Playlist.FIRST)
            R.id.second_playlist -> viewModel.loadPlaylist(Playlist.SECOND)
            R.id.third_playlist -> viewModel.loadPlaylist(Playlist.THIRD)
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
