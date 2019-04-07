package com.leshchenko.youtubefeed.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(application: Application): AndroidViewModel(application), CoroutineScope {

    private val coroutineJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancelChildren()
    }
}