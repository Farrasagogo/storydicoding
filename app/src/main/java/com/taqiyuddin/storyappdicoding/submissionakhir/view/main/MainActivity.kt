package com.taqiyuddin.storyappdicoding.submissionakhir.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.StoryAppPreferences
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.dataStore
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ActivityMainBinding
import com.taqiyuddin.storyappdicoding.submissionakhir.view.landingpage.LandingPageActivity
import com.taqiyuddin.storyappdicoding.submissionakhir.view.story.StoryActivity
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.LocaleUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val preferences: StoryAppPreferences by lazy {
        StoryAppPreferences.getInstance(this.dataStore)
    }

    override fun attachBaseContext(newBase: Context) {
        val preferences = StoryAppPreferences.getInstance(newBase.dataStore)
        val languageCode = runBlocking { preferences.getLanguage().first() }
        super.attachBaseContext(LocaleUtils.setLocale(newBase, languageCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyLanguageConfiguration()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginStatus()
    }

    private fun applyLanguageConfiguration() {
        val languageCode = runBlocking { preferences.getLanguage().first() }
        val context = LocaleUtils.setLocale(this, languageCode)
        resources.configuration.setTo(context.resources.configuration)
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            val isLoggedIn = preferences.isLoggedIn().first()
            if (isLoggedIn) {
                navigateToStoryActivity()
            } else {
                navigateToLandingPage()
            }
        }
    }

    private fun navigateToStoryActivity() {
        val intent = Intent(this, StoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLandingPage() {
        val intent = Intent(this, LandingPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}