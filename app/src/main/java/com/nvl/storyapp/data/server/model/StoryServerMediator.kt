package com.nvl.storyapp.data.server.model

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nvl.storyapp.data.local.database.ServerKeys
import com.nvl.storyapp.data.local.database.StoryDatabase
import com.nvl.storyapp.data.server.response.StoryItem
import com.nvl.storyapp.api.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryServerMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val bearer : String
) : RemoteMediator<Int, StoryItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItem>
    ): MediatorResult {
        val page = when(loadType){
            LoadType.REFRESH ->{
                val serverKeys = getRemoteKeyClosestToCurrentPosition(state)
                serverKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val serverKeys = getRemoteKeyForFirstItem(state)
                val prevKey = serverKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = serverKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val serverKeys = getRemoteKeyForLastItem(state)
                val nextKey = serverKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = serverKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getAllStories(bearer, page, state.config.pageSize)

            val endOfPaginationReached = responseData.listStory?.isEmpty()

            database.withTransaction{
                if (loadType == LoadType.REFRESH) {
                    database.ServerKeysDao().deleteServerKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached == true) null else page + 1
                val keys = responseData.listStory?.map {
                    ServerKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                keys?.let { database.ServerKeysDao().insertAll(it) }
                responseData.listStory?.let { database.storyDao().insertStory(it) }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached == true)
        }catch (exception: Exception){
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryItem>): ServerKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.ServerKeysDao().getServerKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryItem>): ServerKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.ServerKeysDao().getServerKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryItem>): ServerKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.ServerKeysDao().getServerKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
