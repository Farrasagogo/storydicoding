package com.taqiyuddin.storyappdicoding.submissionakhir.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.LoginResponse
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.ApiConfig
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> get() = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            wrapEspressoIdlingResource {
                try {
                    val response = ApiConfig.createApiService().authenticateUser(email, password)
                    if (response.isSuccessful) {
                        _loginResult.value = response.body()
                    } else {
                        _error.value = response.message()
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun resetError() {
        _error.value = null
    }
}

