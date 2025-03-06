package com.taqiyuddin.storyappdicoding.submissionakhir.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.CreateUserAccountResponse
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.ApiConfig
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val _registerResult = MutableLiveData<CreateUserAccountResponse>()
    val registerResult: LiveData<CreateUserAccountResponse> get() = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun signUp(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.createApiService().createUserAccount(name, email, password)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                } else {
                    _error.value = response.message()
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