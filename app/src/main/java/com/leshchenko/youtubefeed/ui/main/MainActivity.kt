package com.leshchenko.youtubefeed.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.leshchenko.youtubefeed.R
import com.leshchenko.youtubefeed.model.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.model.local.models.Playlist
import com.leshchenko.youtubefeed.model.network.RetrofitService
import com.leshchenko.youtubefeed.ui.BaseActivity
import com.leshchenko.youtubefeed.util.PaginationScrollListener
import com.leshchenko.youtubefeed.util.isOnline
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val adapter by lazy {
        PlaylistItemsRecyclerAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupUI(savedInstanceState)
        viewModel.loadPlaylist(if (savedInstanceState == null) null else viewModel.currentPlaylist)
    }

    private fun setupUI(savedInstanceState: Bundle?) {
        progressBar.visibility = VISIBLE
        setupRecyclerView()
        retryButton.setOnClickListener {
            isOnline(this, makeIsOnline = {
                displayLoading()
                viewModel.reloadCurrentPlaylist()
            })
        }
        defineObservers()
        setupDrawer(savedInstanceState)
    }

    private fun setupRecyclerView() {
        adapter.setItemClickListener { playVideo(it) }
        playlistItemsRecycleView.adapter = adapter
        playlistItemsRecycleView.addOnScrollListener(PaginationScrollListener {
            isOnline(this@MainActivity, makeIsOnline = {
                viewModel.loadMoreItems()
            }, makeIsOffline = {})
        })
    }

    private fun defineObservers() {
        viewModel.displayError.observe(this, Observer {
            if (it.first) {
                displayError(it.second)
            } else {
                errorGroup.visibility = GONE
            }
        })

        viewModel.playlistItems.observe(this, Observer {
            displayItems(it)
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
        progressBar.visibility = GONE
        errorGroup.visibility = GONE
        playlistItemsRecycleView.visibility = VISIBLE
        adapter.submitList(items)
    }

    private fun displayError(text: String) {
        progressBar.visibility = GONE
        errorGroup.visibility = VISIBLE
        playlistItemsRecycleView.visibility = GONE
        errorTextView.text = text
    }

    private fun displayLoading() {
        playlistItemsRecycleView.visibility = GONE
        progressBar.visibility = VISIBLE
        errorGroup.visibility = GONE
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displayLoading()
        when (item.itemId) {
            R.id.first_playlist -> viewModel.loadPlaylist(Playlist.FIRST)
            R.id.second_playlist -> viewModel.loadPlaylist(Playlist.SECOND)
            R.id.third_playlist -> viewModel.loadPlaylist(Playlist.THIRD)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
