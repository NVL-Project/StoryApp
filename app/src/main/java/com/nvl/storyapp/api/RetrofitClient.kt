package com.nvl.storyapp.api

import com.nvl.storyapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val EXTRA_URL = BuildConfig.API_URL
object RetrofitClient {
    private val okhttp = OkHttpClient.Builder()
        .apply {
            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG){
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(loggingInterceptor)
            }
        }
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(EXTRA_URL)
        .client(okhttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInstant: ApiService = retrofit.create(ApiService::class.java)
}