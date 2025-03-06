package com.taqiyuddin.storyappdicoding.submissionakhir.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stories")
data class DetailedStory(

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lat")
	val lat: Double? = null
)

data class StoriesResponse(

	@field:SerializedName("listStory")
	val storyList: List<DetailedStory>? = null,

	@field:SerializedName("error")
	val isError: Boolean? = null,

	@field:SerializedName("message")
	val statusMessage: String? = null
)

data class SubmitStoriesResponse(

	@field:SerializedName("error")
	val isError: Boolean? = null,

	@field:SerializedName("message")
	val statusMessage: String? = null
)

data class StoriesDetailResponse(

	@field:SerializedName("error")
	val isError: Boolean? = null,

	@field:SerializedName("message")
	val statusMessage: String? = null,

	@field:SerializedName("story")
	val detailedStory: DetailedStory? = null
)
