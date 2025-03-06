package com.taqiyuddin.storyappdicoding.submissionakhir.view.story

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taqiyuddin.storyappdicoding.submissionakhir.di.RepositoryInjector
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.ApiConfigHeader
import kotlinx.coroutines.launch

class DetailStoryViewModel(
    private val token: String,
    private val storyId: String
) : ViewModel() {
    private val _story = MutableLiveData<DetailedStory?>()
    val story: LiveData<DetailedStory?> get() = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        getStoryDetail()
    }

    private fun getStoryDetail() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val apiService = ApiConfigHeader.createApiService(token)
                val response = apiService.getStoryDetails(storyId)
                if (response.isSuccessful) {
                    _story.value = response.body()?.detailedStory
                } else {
                    _error.value = "Failed to get story: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class DetailStoryViewModelFactory(
    private val context: Context,
    private val storyId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            val token = RepositoryInjector.provideUserToken(context)
            @Suppress("UNCHECKED_CAST")
            return DetailStoryViewModel(token, storyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}