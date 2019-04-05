package com.leshchenko.youtubefeed.util

import android.content.Context
import android.net.ConnectivityManager

fun isOnline(context: Context): Boolean {
    return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo?.isConnected == true
}