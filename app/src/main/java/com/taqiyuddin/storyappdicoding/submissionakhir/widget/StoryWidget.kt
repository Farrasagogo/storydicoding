package com.taqiyuddin.storyappdicoding.submissionakhir.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.taqiyuddin.storyappdicoding.submissionakhir.view.story.DetailStoryActivity
import com.taqiyuddin.storyappdicoding.submissionakhir.R

@Suppress("DEPRECATION")
class StoryWidget : AppWidgetProvider() {

    companion object {
        private const val STORY_ACTION = "com.taqiyuddin.storyappdicoding.submissionawal.widget.StoryWidget.STORY_ACTION"
        const val EXTRA_ITEM = "com.taqiyuddin.storyappdicoding.submissionawal.widget.StoryWidget.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.story_widget).apply {
                setRemoteAdapter(R.id.stack_view, intent)
                setEmptyView(R.id.stack_view, R.id.empty_view)
            }

            val storyIntent = Intent(context, StoryWidget::class.java).apply {
                action = STORY_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, storyIntent, flags
            )

            views.setPendingIntentTemplate(R.id.stack_view, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == STORY_ACTION) {
            val storyId = intent.getStringExtra(EXTRA_ITEM)
            val detailIntent = Intent(context, DetailStoryActivity::class.java).apply {
                putExtra(DetailStoryActivity.EXTRA_STORY_ID, storyId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(detailIntent)
        }
    }
}