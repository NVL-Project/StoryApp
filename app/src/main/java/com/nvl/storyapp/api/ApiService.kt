package com.nvl.storyapp.api

import com.nvl.storyapp.data.server.response.AddStoryResponse
import com.nvl.storyapp.data.server.response.ListStoryResponse
import com.nvl.storyapp.data.server.response.LoginResponse
import com.nvl.storyapp.data.server.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("/v1/stories")
    fun uploadStory(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<AddStoryResponse>

    @FormUrlEncoded
    @POST("/v1/register")
    fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<LoginResponse>

    @GET("/v1/stories")
    suspend fun getAllStories(
        @Header("Authorization") bearer: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): ListStoryResponse

    @GET("/v1/stories")
    fun getMaps(
        @Header("Authorization") bearer: String?,
        @Query("location") page: Int = 1,
    ): Call<ListStoryResponse>
}