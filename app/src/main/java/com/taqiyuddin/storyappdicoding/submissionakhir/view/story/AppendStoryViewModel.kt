package com.taqiyuddin.storyappdicoding.submissionakhir.view.story

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taqiyuddin.storyappdicoding.submissionakhir.di.RepositoryInjector
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.SubmitStoriesResponse
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.ApiConfigHeader
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AppendStoryViewModel(private val token: String) : ViewModel() {
    private val _createStoryResponse = MutableLiveData<SubmitStoriesResponse>()
    val createStoryResponse: MutableLiveData<SubmitStoriesResponse> get() = _createStoryResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> get() = _error

    fun uploadStory(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody,
        long: RequestBody,
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response =
                    ApiConfigHeader.createApiService(token)
                        .uploadStory(description, photo, lat, long)
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _createStoryResponse.value = response.body()
                } else {
                    _isLoading.value = false
                    _error.value = "Failed to create story: ${response.message()}"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
            }
        }
    }

    fun resetError() {
        _error.value = null
    }
}

class CreateStoryViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppendStoryViewModel::class.java)) {
            val token = RepositoryInjector.provideUserToken(context)
            @Suppress("UNCHECKED_CAST")
            return AppendStoryViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}