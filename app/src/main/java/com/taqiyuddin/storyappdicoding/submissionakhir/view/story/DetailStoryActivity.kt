package com.taqiyuddin.storyappdicoding.submissionakhir.view.story

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var viewModel: DetailStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        observeViewModel()
    }

    private fun setupViewModel() {
        val storyId = intent.getStringExtra(EXTRA_STORY_ID) ?: ""
        viewModel = ViewModelProvider(
            this,
            DetailStoryViewModelFactory(this, storyId)
        )[DetailStoryViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.story.observe(this) { story ->
            story?.let {
                binding.apply {
                    tvDetailName.text = it.name
                    tvDetailDescription.text = it.description

                    Glide.with(this@DetailStoryActivity)
                        .load(it.photoUrl)
                        .into(ivDetailPhoto)
                }
            }
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}