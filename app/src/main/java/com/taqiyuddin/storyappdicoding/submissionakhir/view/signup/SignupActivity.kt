package com.taqiyuddin.storyappdicoding.submissionakhir.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.StoryAppPreferences
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.dataStore
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ActivitySignupBinding
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.displayToast
import com.taqiyuddin.storyappdicoding.submissionakhir.view.customview.EmailEditText
import com.taqiyuddin.storyappdicoding.submissionakhir.view.customview.PasswordEditText
import com.taqiyuddin.storyappdicoding.submissionakhir.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var nameEditText: com.google.android.material.textfield.TextInputEditText
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var signupButton: MaterialButton
    private lateinit var preferences: StoryAppPreferences
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = StoryAppPreferences.getInstance(dataStore)

        setupView()
        setupAction()
        playAnimation()
        observeViewModel()
    }

    private fun setupView() {
        nameEditText = binding.edRegisterName
        emailEditText = binding.edRegisterEmail
        passwordEditText = binding.edRegisterPassword
        signupButton = binding.signupButton

        nameEditText.addTextChangedListener {
            checkEditTextErrors()
        }

        emailEditText.addTextChangedListener {
            checkEditTextErrors()
        }

        passwordEditText.addTextChangedListener {
            checkEditTextErrors()
        }


    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            name = nameEditText.text.toString()
            email = emailEditText.text.toString()
            password = passwordEditText.text.toString()

            viewModel.signUp(name, email, password)
        }
    }

    private fun checkEditTextErrors() {
        name = nameEditText.text.toString()
        email = emailEditText.text.toString()
        password = passwordEditText.text.toString()

        if (password.isEmpty()) {
            passwordEditText.error = null
        }

        signupButton.isEnabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
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

        viewModel.registerResult.observe(this) { response ->
            response?.let { registerResponse ->
                if (registerResponse.isError == false) {
                    displayToast(this, registerResponse.statusMessage.toString())
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    displayToast(this, registerResponse.statusMessage.toString())
                }
            }
        }
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

        val titleAnimator = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).apply {
            duration = 400
            startDelay = 100
        }

        val messageAnimator = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 0f, 1f).apply {
            duration = 400
            startDelay = 200
        }

        val nameTextViewAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.nameTextView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 50f, 0f)
        ).apply {
            duration = 400
            startDelay = 300
        }

        val nameEditTextAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.nameEditTextLayout,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f)
        ).apply {
            duration = 400
            startDelay = 400
        }

        val emailTextViewAnimator = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 0f, 1f).apply {
            duration = 400
            startDelay = 500
        }

        val emailEditTextAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.emailEditTextLayout,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.ROTATION, -5f, 0f)
        ).apply {
            duration = 400
            startDelay = 600
        }

        val passwordTextViewAnimator = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 0f, 1f).apply {
            duration = 400
            startDelay = 700
        }

        val passwordEditTextAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.passwordEditTextLayout,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 50f, 0f)
        ).apply {
            duration = 400
            startDelay = 800
        }

        val signupButtonAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.signupButton,
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
                messageAnimator,
                nameTextViewAnimator,
                nameEditTextAnimator,
                emailTextViewAnimator,
                emailEditTextAnimator,
                passwordTextViewAnimator,
                passwordEditTextAnimator,
                signupButtonAnimator
            )
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}