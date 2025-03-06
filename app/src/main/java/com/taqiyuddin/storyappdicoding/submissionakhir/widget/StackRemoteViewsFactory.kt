package com.taqiyuddin.storyappdicoding.submissionakhir.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.ViewModelProvider
import com.taqiyuddin.storyappdicoding.submissionakhir.R
import com.taqiyuddin.storyappdicoding.submissionakhir.di.RepositoryInjector
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.loadWidgetImage
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private val widgetStoriesList = mutableListOf<DetailedStory>()
    private lateinit var userToken: String
    private lateinit var widgetViewModel: WidgetViewModel

    override fun onCreate() {
        widgetViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as android.app.Application)
            .create(WidgetViewModel::class.java)
        userToken = RepositoryInjector.provideUserToken(context)
    }

    override fun onDataSetChanged() {
        runBlocking {
            try {
                val stories = widgetViewModel.getWidgetStories(userToken)
                widgetStoriesList.clear()
                stories?.let { widgetStoriesList.addAll(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        widgetStoriesList.clear()
    }

    override fun getCount(): Int = widgetStoriesList.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position !in widgetStoriesList.indices) {
            return RemoteViews(context.packageName, R.layout.item_widget)
        }

        val story = widgetStoriesList[position]
        val views = RemoteViews(context.packageName, R.layout.item_widget).apply {
            setTextViewText(R.id.story_user_name, story.name)
            story.photoUrl?.let { loadWidgetImage(it)?.let { image -> setImageViewBitmap(R.id.story_image, image) } }
        }

        val intent = Intent().apply {
            putExtra(StoryWidget.EXTRA_ITEM, story.id)
        }
        views.setOnClickFillInIntent(R.id.item_widget, intent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}