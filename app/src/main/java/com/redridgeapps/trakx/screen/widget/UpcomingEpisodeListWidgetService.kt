package com.redridgeapps.trakx.screen.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViewsService
import com.redridgeapps.trakx.Database
import dagger.android.AndroidInjection
import javax.inject.Inject

class UpcomingEpisodeListWidgetService : RemoteViewsService() {

    @Inject
    lateinit var database: Database

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return UpcomingEpisodeViewsFactory(packageName, database)
    }

    companion object {

        fun createIntent(context: Context, appWidgetId: Int): Intent {
            return Intent(context, UpcomingEpisodeListWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
        }
    }
}
