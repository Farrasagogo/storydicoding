package com.taqiyuddin.storyappdicoding.submissionakhir.view.story

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.taqiyuddin.storyappdicoding.submissionakhir.data.repositories.StoryRepository
import com.taqiyuddin.storyappdicoding.submissionakhir.di.RepositoryInjector
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory

class StoryViewModel(storyRepository: StoryRepository) : ViewModel() {

    val stories: LiveData<PagingData<DetailedStory>> =
        storyRepository.getPagingStories().cachedIn(viewModelScope)
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(RepositoryInjector.provideStoryRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

