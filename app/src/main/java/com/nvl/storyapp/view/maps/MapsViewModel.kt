package com.nvl.storyapp.view.maps

import androidx.lifecycle.ViewModel
import com.nvl.storyapp.data.server.model.Repository

class MapsViewModel (private val ref: Repository): ViewModel(){
    fun getMaps(bearer: String) = ref.getStoryMaps(bearer)
}