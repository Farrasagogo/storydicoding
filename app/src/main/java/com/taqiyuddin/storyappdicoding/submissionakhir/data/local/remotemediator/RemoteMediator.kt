package com.taqiyuddin.storyappdicoding.submissionakhir.data.local.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.database.ContentDatabase
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.StoryApiService
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.keys.PagingKeys

@OptIn(ExperimentalPagingApi::class)
class RemoteMediator(
    private val database: ContentDatabase,
    private val apiService: StoryApiService,
) : RemoteMediator<Int, DetailedStory>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DetailedStory>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextPageKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.previousPageKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextPageKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = apiService.fetchAllStories(page, state.config.pageSize, 0)
            val responseData = response.body()?.storyList ?: emptyList()
            val endOfPaginationReached = responseData.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.pagingKeysDao().clearPagingKeys()
                    database.contentDao().clearStories()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    PagingKeys(itemId = it.id, previousPageKey = prevKey, nextPageKey = nextKey)
                }
                database.pagingKeysDao().savePagingKeys(keys)

                database.contentDao().saveStories(responseData)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DetailedStory>): PagingKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            data.id.let { database.pagingKeysDao().getPagingKey(it) }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, DetailedStory>): PagingKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            data.id.let { database.pagingKeysDao().getPagingKey(it) }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, DetailedStory>): PagingKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.pagingKeysDao().getPagingKey(id)
            }
        }
    }
}