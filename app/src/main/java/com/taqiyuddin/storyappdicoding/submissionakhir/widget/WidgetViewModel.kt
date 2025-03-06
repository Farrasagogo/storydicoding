package com.taqiyuddin.storyappdicoding.submissionakhir.widget

import androidx.lifecycle.ViewModel
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.data.api.ApiConfigHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WidgetViewModel : ViewModel() {
    suspend fun getWidgetStories(token: String): List<DetailedStory>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiConfigHeader.createApiService(token)
                    .fetchAllStories(pageNumber = 1, pageSize = 10, includeLocation = 0)
                if (response.isSuccessful) {
                    response.body()?.storyList
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
