package com.taqiyuddin.storyappdicoding.submissionakhir.view.landingpage

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ActivityLandingPageBinding
import com.taqiyuddin.storyappdicoding.submissionakhir.view.login.LoginActivity
import com.taqiyuddin.storyappdicoding.submissionakhir.view.signup.SignupActivity
import androidx.activity.viewModels


class LandingPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingPageBinding
    private val viewModel: LandingPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()

        viewModel.startAnimation()
    }

    private fun setupViews() {
        binding.apply {
            loginHomeButton.setOnClickListener {
                navigateToLogin()
            }

            signupHomeButton.setOnClickListener {
                navigateToRegister()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.animationState.observe(this) { state ->
            when (state) {
                is LandingPageViewModel.AnimationState.START -> playAnimation()
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, SignupActivity::class.java))
    }

    private fun playAnimation() {
        val animDuration = 500L

        val animations = listOf(
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).apply { duration = animDuration },
            ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).apply { duration = animDuration },
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(binding.loginHomeButton, View.ALPHA, 1f).apply { duration = animDuration },
                    ObjectAnimator.ofFloat(binding.signupHomeButton, View.ALPHA, 1f).apply { duration = animDuration }
                )
            }
        )

        AnimatorSet().apply {
            playSequentially(animations)
            start()
        }
    }
}