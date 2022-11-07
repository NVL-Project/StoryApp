package com.nvl.storyapp.data.server.model

import android.content.Context
import com.nvl.storyapp.data.local.UserPreference
import com.nvl.storyapp.data.local.database.StoryDatabase
import com.nvl.storyapp.api.RetrofitClient

object Injection {
    fun provideRepository(context: Context): Repository {
        val userPreferences = UserPreference.getInstance(context)
        val remoteDataSource = ServerDataSource.getInstance()
        val database = StoryDatabase.getDatabase(context)
        val apiService = RetrofitClient.apiInstant
        return Repository.getInstance(remoteDataSource, userPreferences, database, apiService)
    }
}