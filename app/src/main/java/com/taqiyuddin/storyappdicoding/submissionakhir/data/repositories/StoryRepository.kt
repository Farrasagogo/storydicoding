package com.taqiyuddin.storyappdicoding.submissionakhir.data.repositories

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.remotemediator.RemoteMediator
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.database.ContentDatabase
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.StoryApiService

class StoryRepository(
    private val pagingDatabase: ContentDatabase,
    private val storyApiSevice: StoryApiService,
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getPagingStories(): LiveData<PagingData<DetailedStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = RemoteMediator(pagingDatabase, storyApiSevice),
            pagingSourceFactory = { pagingDatabase.contentDao().loadStoriesWithPaging() }
        ).liveData
    }

}