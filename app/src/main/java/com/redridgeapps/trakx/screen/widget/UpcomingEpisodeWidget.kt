package com.redridgeapps.trakx.screen.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.redridgeapps.trakx.R

class UpcomingEpisodeWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds)
            updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    companion object {

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            val intent = UpcomingEpisodeListWidgetService.createIntent(context, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.upcoming_episode_widget)
            views.setTextViewText(R.id.widget_header, context.getApplicationName())
            views.setRemoteAdapter(R.id.episode_list, intent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.episode_list)
        }

        private fun Context.getApplicationName(): String {
            val applicationInfo = applicationInfo
            val stringId = applicationInfo.labelRes
            return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
        }
    }
}
