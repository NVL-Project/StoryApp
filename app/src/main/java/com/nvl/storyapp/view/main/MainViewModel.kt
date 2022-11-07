package com.nvl.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nvl.storyapp.data.server.model.Repository
import com.nvl.storyapp.data.server.response.StoryItem

class MainViewModel(private val pref: Repository) : ViewModel() {
    fun getStories(bearer: String) : LiveData<PagingData<StoryItem>> =
        pref.getStories(bearer).cachedIn(viewModelScope)
    fun deleteUser() = pref.deleteUser()
}