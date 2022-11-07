package com.nvl.storyapp.data.server.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListStoryResponse (
    @field:SerializedName("listStory")
    val listStory: List<StoryItem>?,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
) : Parcelable