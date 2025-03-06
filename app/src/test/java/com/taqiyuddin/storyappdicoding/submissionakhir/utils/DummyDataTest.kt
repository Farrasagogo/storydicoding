package com.taqiyuddin.storyappdicoding.submissionakhir.utils

import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory

object DummyDataTest {
    fun generateDummyStoryResponse(): List<DetailedStory> {
        val items: MutableList<DetailedStory> = arrayListOf()
        for (i in 0..30) {
            val story = DetailedStory(
                id = i.toString(),
                name = "name $i",
                description = "desc $i",
                createdAt = "2024-12-$i",
                photoUrl = "https://i.postimg.cc/SNnNTwZr/dicodinglogo.png",
                lat = 8.23,
                lon = 113.75,
            )
            items.add(story)
        }
        return items
    }
}