package com.taqiyuddin.storyappdicoding.submissionakhir.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStories(stories: List<DetailedStory>)

    @Query("SELECT * FROM stories")
    fun loadStoriesWithPaging(): PagingSource<Int, DetailedStory>

    @Query("DELETE FROM stories")
    suspend fun clearStories()
}