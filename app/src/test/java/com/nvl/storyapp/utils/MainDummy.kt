package com.nvl.storyapp.utils

import com.nvl.storyapp.data.server.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object MainDummy {
    fun generateDummyStory(): List<StoryItem>{
        val newList = ArrayList<StoryItem>()

        for(i in 0..10){
            val user = StoryItem(
                "story-X5czsS-koI8Qn8gQ$i",
                "tes123456",
                "tes gambar",
                "https://story-api.dicoding.dev/images/stories/photos-1667097038541_GYgSp-ku.jpg",
                "2022-10-30T02:30:38.544Z",
                i.toDouble(),i.toDouble()
            )
            newList.add(user)
        }
        return newList
    }
    fun generateDummyStoryResponse(): ListStoryResponse {
        val error = false
        val message = "Stories fetched successfully"
        val listStory = ArrayList<StoryItem>()

        for (i in 0 until 10) {
            val story = StoryItem(
                "story-X5czsS-koI8Qn8gQ$i",
                "tes123456",
                "tes gambar",
                "https://story-api.dicoding.dev/images/stories/photos-1667097038541_GYgSp-ku.jpg",
                "2022-10-30T02:30:38.544Z",
                i.toDouble(),i.toDouble()
            )

            listStory.add(story)
        }

        return ListStoryResponse(listStory, error, message)
    }
    fun generateDummyLogin(): LoginResponse {
        val user = LoginResult(
            "user-POMx0JfdJ81yyOKP",
            "Cobak",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVBPTXgwSmZkSjgxeXlPS1AiLCJpYXQiOjE2NjcxNDk2NDd9.TrbiwAXZMgR2FpbTLOrDh0XAYp0uFgGCHt-S_dQ73iU",
        )
        return LoginResponse(
            loginResult = user,
            error = false,
            message = "success"
        )
    }
    fun generateDummyLoginResult(): LoginResult {
        return LoginResult(
            "user-POMx0JfdJ81yyOKP",
            "Cobak",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVBPTXgwSmZkSjgxeXlPS1AiLCJpYXQiOjE2NjcxNDk2NDd9.TrbiwAXZMgR2FpbTLOrDh0XAYp0uFgGCHt-S_dQ73iU",
        )
    }
    fun generateDummyRegister(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }
    fun generateDummyAddStoryResponse(): AddStoryResponse {
        return AddStoryResponse(
            error = false,
            message = "success"
        )
    }
    fun generateDummyMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyDesc(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }
    fun generateDummylat(): RequestBody {
        val dummyText = "0.0"
        return dummyText.toRequestBody()
    }
    fun generateDummylng(): RequestBody {
        val dummyText = "0.0"
        return dummyText.toRequestBody()
    }

}