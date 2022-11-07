package com.nvl.storyapp.data.server.model

import com.nvl.storyapp.api.RetrofitClient
import com.nvl.storyapp.data.server.response.AddStoryResponse
import com.nvl.storyapp.data.server.response.ListStoryResponse
import com.nvl.storyapp.data.server.response.LoginResponse
import com.nvl.storyapp.data.server.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServerDataSource {
    fun login(callback: LoginCallbackState, email: String, password: String){
        val client = RetrofitClient.apiInstant.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    response.body()?.let { callback.onLogin(it) }
                }else {
                    val loginError = LoginResponse(
                        null,
                        true,
                        "Login failed!"
                    )
                    callback.onLogin(
                        loginError
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val loginError = LoginResponse(
                    null,
                    true,
                    t.message.toString()
                )
                callback.onLogin(
                    loginError
                )
            }
        })
    }

    fun register(callback: RegisterCallbackState, name: String, email: String, password: String){
        val client = RetrofitClient.apiInstant.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if(response.isSuccessful){
                    response.body()?.let { callback.onRegister(it) }
                }else{
                    val registerError = RegisterResponse(
                        true,
                        "Registration failed!"
                    )
                    callback.onRegister(
                        registerError
                    )
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                val registerError = RegisterResponse(
                    true,
                    t.message.toString()
                )
                callback.onRegister(
                    registerError
                )
            }

        })
    }

    fun uploadFile(
        callback: UploadCallbackState,
        bearer: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ){
        val client = RetrofitClient.apiInstant.uploadStory(bearer, file, description, lat, lon)

        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        callback.onUploaded(responseBody)
                    }else{
                        callback.onUploaded(
                            uploadResponse = AddStoryResponse(
                                true,
                                "Failed to upload file!"
                            )
                        )
                    }
                }
                else{
                    callback.onUploaded(
                        uploadResponse = AddStoryResponse(
                            true,
                            "Failed to upload file!"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                callback.onUploaded(
                    uploadResponse = AddStoryResponse(
                        true,
                        "Failed to upload file!"
                    )
                )
            }
        })
    }

    fun getStoryMaps(callback: StoryMapCallbackState, bearer: String){
        val client =  RetrofitClient.apiInstant.getMaps(bearer)

        client.enqueue(object : Callback<ListStoryResponse>{
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful){
                    response.body()?.let { callback.onStoryMapLoaded(it) }
                }else{
                    val storiesResponse = ListStoryResponse(
                        null,
                        true,
                        "Failed to load location story!"
                    )
                    callback.onStoryMapLoaded(storiesResponse)
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                val storiesResponse = ListStoryResponse(
                    null,
                    true,
                    "Failed to load location story!"
                )
                callback.onStoryMapLoaded(storiesResponse)
            }
        })
    }
    interface LoginCallbackState{
        fun onLogin(loginResponse: LoginResponse)
    }

    interface RegisterCallbackState{
        fun onRegister(registerResponse: RegisterResponse)
    }

    interface UploadCallbackState{
        fun onUploaded(uploadResponse: AddStoryResponse)
    }

    interface StoryMapCallbackState{
        fun onStoryMapLoaded(storyMaps : ListStoryResponse)
    }

    companion object {
        @Volatile
        private var instance: ServerDataSource? = null

        fun getInstance(): ServerDataSource =
            instance ?: synchronized(this) {
                instance ?: ServerDataSource()
            }
    }
}