package com.taqiyuddin.storyappdicoding.submissionakhir.view.maps

import android.content.Context
import androidx.lifecycle.*
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.ApiConfigHeader
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.di.RepositoryInjector
import kotlinx.coroutines.launch

class MapsViewModel(private val token: String) : ViewModel() {

    private val _stories = MutableLiveData<List<DetailedStory>?>()
    val stories: LiveData<List<DetailedStory>?> get() = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun getStoriestMap() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfigHeader.createApiService(token).fetchStoriesWithLocation()
                if (response.isSuccessful) {
                    _stories.value = response.body()?.storyList
                } else {
                    _error.value = "Failed to get stories: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class MapsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            val token = RepositoryInjector.provideUserToken(context)
            @Suppress("UNCHECKED_CAST")
            return MapsViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}