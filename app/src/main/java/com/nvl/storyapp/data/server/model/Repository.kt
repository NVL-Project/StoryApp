package com.nvl.storyapp.data.server.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.nvl.storyapp.data.local.UserPreference
import com.nvl.storyapp.data.local.database.StoryDatabase
import com.nvl.storyapp.api.ApiService
import com.nvl.storyapp.data.server.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository constructor(
    private val ServerDataSource: ServerDataSource,
    private val UserPreference: UserPreference,
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

     fun login(email: String, password: String): LiveData<LoginResponse> {
        val loginResponses = MutableLiveData<LoginResponse>()

        ServerDataSource.login(object : ServerDataSource.LoginCallbackState{
            override fun onLogin(loginResponse: LoginResponse) {
                loginResponses.postValue(loginResponse)
                if (!loginResponse.error){
                    loginResponse.loginResult?.let { UserPreference.setUser(it) }
                }
            }
        }, email, password)
        return loginResponses
    }

     fun register(name: String, email: String, password: String): LiveData<RegisterResponse> {
        val registerResponses = MutableLiveData<RegisterResponse>()

        ServerDataSource.register(object : ServerDataSource.RegisterCallbackState{
            override fun onRegister(registerResponse: RegisterResponse) {
                registerResponses.postValue(registerResponse)
            }
        }, name, email, password)
        return registerResponses
    }

     fun getUser(): LiveData<LoginResult> {
        val user = MutableLiveData<LoginResult>()
        user.postValue(UserPreference.getUser())
        return user
    }

    fun deleteUser() {
        UserPreference.deleteUser()
    }

     fun getStories(bearer: String): LiveData<PagingData<StoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryServerMediator(storyDatabase, apiService, bearer),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

     fun uploadFile(
        bearer: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): LiveData<AddStoryResponse> {
        val uploadResponseStatus = MutableLiveData<AddStoryResponse>()

        ServerDataSource.uploadFile(object : ServerDataSource.UploadCallbackState{
            override fun onUploaded(uploadResponse: AddStoryResponse) {
                uploadResponseStatus.postValue(uploadResponse)
            }
        }, bearer, file, description, lat, lon)
        return uploadResponseStatus
    }
     fun getStoryMaps(bearer: String): LiveData<ListStoryResponse> {
        val mapResponseStatus = MutableLiveData<ListStoryResponse>()

        ServerDataSource.getStoryMaps(object : ServerDataSource.StoryMapCallbackState{
            override fun onStoryMapLoaded(storyMaps: ListStoryResponse) {
                mapResponseStatus.postValue(storyMaps)
            }
        }, bearer)
        return mapResponseStatus
    }
    companion object {
        @Volatile
        private var instance: Repository? = null

        @JvmStatic
        fun getInstance(
            ServerDataSource: ServerDataSource,
            pref: UserPreference,
            storyDatabase: StoryDatabase,
            apiService: ApiService
        ) : Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(ServerDataSource, pref, storyDatabase, apiService)
            }.also { instance = it }
    }
}