package com.taqiyuddin.storyappdicoding.submissionakhir.di

import android.content.Context
import com.taqiyuddin.storyappdicoding.submissionakhir.data.repositories.StoryRepository
import com.taqiyuddin.storyappdicoding.submissionakhir.data.local.database.ContentDatabase
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.StoryAppPreferences
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.dataStore
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.ApiConfigHeader
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object RepositoryInjector {
    fun provideStoryRepository(context: Context): StoryRepository {
        val userToken = provideUserToken(context)
        val storyApiService = ApiConfigHeader.createApiService(userToken)
        val database = ContentDatabase.getDatabase(context)
        return StoryRepository(database, storyApiService)
    }
    fun provideUserToken(context: Context): String {
        val preferences = StoryAppPreferences.getInstance(context.dataStore)
        val userToken: String = runBlocking { preferences.getToken().first() }
        return userToken
    }
}