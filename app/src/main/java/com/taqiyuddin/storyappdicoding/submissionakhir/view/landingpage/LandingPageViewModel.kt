package com.taqiyuddin.storyappdicoding.submissionakhir.view.landingpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LandingPageViewModel : ViewModel() {
    private val _animationState = MutableLiveData<AnimationState>()
    val animationState: LiveData<AnimationState> = _animationState

    fun startAnimation() {
        _animationState.value = AnimationState.START
    }

    sealed class AnimationState {
        data object START : AnimationState()
    }
}
