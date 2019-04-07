package com.leshchenko.youtubefeed.util

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.leshchenko.youtubefeed.R

fun isOnline(context: Context, makeIsOnline: (() -> Unit)? = null, makeIsOffline: (() -> Unit)? = null): Boolean {
    val isOnline = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo?.isConnected == true
    if (isOnline) {
        makeIsOnline?.invoke()
    } else {
        makeIsOffline?.invoke() ?: Toast.makeText(context, context.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
    }
    return isOnline
}