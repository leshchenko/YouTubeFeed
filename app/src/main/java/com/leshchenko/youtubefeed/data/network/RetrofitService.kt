package com.leshchenko.youtubefeed.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {
    companion object {
        private const val baseUrl = "https://www.googleapis.com/"
        const val apiKey = "AIzaSyDyS3gd-lTycnSZUWimdcda-S2KuVa4SyI"

         val retrofit: Retrofit by lazy {
             Retrofit.Builder()
                 .baseUrl(baseUrl)
                 .addConverterFactory(GsonConverterFactory.create())
                 .build()
         }
    }
}