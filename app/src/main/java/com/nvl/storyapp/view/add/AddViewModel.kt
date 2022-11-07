package com.nvl.storyapp.view.add

import androidx.lifecycle.ViewModel
import com.nvl.storyapp.data.server.model.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel (private val pref: Repository): ViewModel(){
    fun uploadStory(bearer: String, file: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody) =
        pref.uploadFile(bearer, file, description, lat, lon)
}