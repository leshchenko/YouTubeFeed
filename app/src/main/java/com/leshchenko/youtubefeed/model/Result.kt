package com.leshchenko.youtubefeed.model

data class Result<out T>(private val data: T? = null, private val error: Throwable? = null) {

    fun getError() = error

    fun withResult(success: (T) -> Unit, error: (Throwable) -> Unit = {}) {
        data?.let(success) ?: this.error?.let(error)
    }
}
