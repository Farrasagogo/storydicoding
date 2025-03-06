package com.taqiyuddin.storyappdicoding.submissionakhir.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ItemStoryBinding
import com.taqiyuddin.storyappdicoding.submissionakhir.view.story.DetailStoryActivity

class StoryAdapter :
    PagingDataAdapter<DetailedStory, StoryAdapter.StoryAdapterViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapterViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapterViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class StoryAdapterViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context

        fun bind(story: DetailedStory) {
            with(binding) {
                tvItemName.text = story.name

                story.description?.let {
                    tvDetailDescription.text = it
                }

                Glide.with(context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(ivItemPhoto)

                root.setOnClickListener {
                    val intent = Intent(context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, story.id)
                    context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetailedStory>() {
            override fun areItemsTheSame(oldItem: DetailedStory, newItem: DetailedStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DetailedStory,
                newItem: DetailedStory,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}