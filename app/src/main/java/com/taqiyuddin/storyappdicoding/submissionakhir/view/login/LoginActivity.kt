package com.taqiyuddin.storyappdicoding.submissionakhir.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.StoryAppPreferences
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.dataStore
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ActivityLoginBinding
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.displayToast
import com.taqiyuddin.storyappdicoding.submissionakhir.view.customview.EmailEditText
import com.taqiyuddin.storyappdicoding.submissionakhir.view.customview.PasswordEditText
import com.taqiyuddin.storyappdicoding.submissionakhir.view.story.StoryActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var preferences: StoryAppPreferences
    private lateinit var email: String
    private lateinit var password: String
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = StoryAppPreferences.getInstance(dataStore)

        setupView()
        playAnimation()
        setupAction()
        observeViewModel()
    }

    private fun setupView() {
        emailEditText = binding.edLoginEmail
        passwordEditText = binding.edLoginPassword
        loginButton = binding.loginButton

        emailEditText.addTextChangedListener {
            checkEditTextErrors()
        }

        passwordEditText.addTextChangedListener {
            checkEditTextErrors()
        }

    }

    private fun setupAction() {
        loginButton.setOnClickListener {
            email = emailEditText.text.toString()
            password = passwordEditText.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun checkEditTextErrors() {
        email = emailEditText.text.toString()
        password = passwordEditText.text.toString()

        if (password.isEmpty()) {
            passwordEditText.error = null
        }

        loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
                && emailEditText.error == null && passwordEditText.error == null
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                displayToast(this, it)
                viewModel.resetError()
            }
        }

        viewModel.loginResult.observe(this) { response ->
            response?.let { loginResponse ->
                if (loginResponse.isError == false && loginResponse.loginDetails != null) {
                    lifecycleScope.launch {
                        preferences.saveSession(
                            loginResponse.loginDetails.authToken ?: "",
                        )
                        val intent = Intent(this@LoginActivity, StoryActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } else {
                    displayToast(this, loginResponse.statusMessage ?: "Login failed")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofPropertyValuesHolder(
            binding.imageView,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -30f, 30f),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -20f, 20f),
            PropertyValuesHolder.ofFloat(View.ROTATION, -10f, 10f),
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f, 1.1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f, 1.1f)
        ).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = android.view.animation.BounceInterpolator()
        }.start()

        val titleAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.titleTextView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 50f, 0f)
        ).apply {
            duration = 400
            startDelay = 100
        }

        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 0f, 1f).apply {
            duration = 400
            startDelay = 500
        }

        val emailEditText = ObjectAnimator.ofPropertyValuesHolder(
            binding.emailEditTextLayout,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.ROTATION, -5f, 0f)
        ).apply {
            duration = 400
            startDelay = 600
        }

        val passwordTv = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 0f, 1f).apply {
            duration = 400
            startDelay = 700
        }

        val passwordEditText = ObjectAnimator.ofPropertyValuesHolder(
            binding.passwordEditTextLayout,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 50f, 0f)
        ).apply {
            duration = 400
            startDelay = 800
        }

        val loginButton = ObjectAnimator.ofPropertyValuesHolder(
            binding.loginButton,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f)
        ).apply {
            duration = 400
            startDelay = 900
        }

        AnimatorSet().apply {
            playSequentially(
                titleAnimator,
                emailTv,
                emailEditText,
                passwordTv,
                passwordEditText,
                loginButton
            )
            startDelay = 200
            start()
        }
    }
}