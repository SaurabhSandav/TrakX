package com.redridgeapps.trakx.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViewsService
import com.redridgeapps.trakx.AppDatabase
import dagger.android.AndroidInjection
import javax.inject.Inject

class UpcomingEpisodeListWidgetService : RemoteViewsService() {

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return UpcomingEpisodeViewsFactory(packageName, appDatabase)
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
