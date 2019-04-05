package com.leshchenko.youtubefeed.ui

import android.os.Bundle
import android.util.Log
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leshchenko.youtubefeed.R
import com.leshchenko.youtubefeed.model.local.models.PlayListItemLocalModel
import com.leshchenko.youtubefeed.model.local.models.Playlist
import com.leshchenko.youtubefeed.model.local.models.PlaylistLocalModel
import com.leshchenko.youtubefeed.util.PlaylistItemsDiffUtil
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
        progressBar.visibility = View.VISIBLE
        playlistItemsRecycleView.adapter = adapter
        playlistItemsRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
                val firstVisibleItemPosition = (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
                if(firstVisibleItemPosition >= totalItemCount - 15) {
                    viewModel.loadMoreItems()
                }
            }
        })
        retryButton.setOnClickListener {
            viewModel.loadPlaylist(null)
        }
        defineObservers()
        setupDrawer()
    }

    private fun defineObservers() {
        viewModel.displayError.observe(this, Observer {
            progressBar.visibility = GONE
            errorGroup.visibility = if (it) VISIBLE else GONE
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
        viewModel.loadPlaylist(null)
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun displayItems(items: List<PlayListItemLocalModel>) {
        errorGroup.visibility = GONE
        val diffCallback = PlaylistItemsDiffUtil(adapter.getData(), items)
        val diffResult = DiffUtil.calculateDiff(diffCallback, false)
        adapter.setData(items)
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun displayNoVideosError() {
        errorGroup.visibility = VISIBLE
        errorTextView.text = getString(R.string.no_items)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_camera -> {
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
