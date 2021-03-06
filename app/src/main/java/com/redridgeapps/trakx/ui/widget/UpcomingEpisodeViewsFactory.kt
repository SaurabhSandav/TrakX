package com.redridgeapps.trakx.ui.widget

import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.redridgeapps.trakx.AppDatabase
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.UpcomingEpisode
import com.redridgeapps.trakx.utils.DateTimeUtils

class UpcomingEpisodeViewsFactory(
    private val packageName: String,
    appDatabase: AppDatabase
) : RemoteViewsService.RemoteViewsFactory {

    private var widgetEpisodeList = mutableListOf<WidgetEpisode>()
    private val trackedShowQueries = appDatabase.trackedShowQueries
    private val upcomingEpisodesQueries = appDatabase.upcomingEpisodesQueries

    override fun onCreate() {}

    override fun onDestroy() {}

    override fun getCount() = widgetEpisodeList.size

    override fun hasStableIds() = true

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int): Long {
        return widgetEpisodeList[position].upcomingEpisode.showId.toLong()
    }

    override fun onDataSetChanged() {

        val episodeList = upcomingEpisodesQueries.upcomingEpisodes().executeAsList()

        widgetEpisodeList.clear()

        episodeList.mapTo(widgetEpisodeList) {
            val trackedShow = trackedShowQueries.getShow(it.showId).executeAsOne()
            WidgetEpisode(trackedShow.name, it)
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val widgetEpisode = widgetEpisodeList[position]

        val episode = widgetEpisode.upcomingEpisode
        val timeToGo = DateTimeUtils.humanReadableTimeToGo(episode.airDate)

        return RemoteViews(packageName, R.layout.upcoming_episode_row).apply {
            setTextViewText(R.id.episode_title, episode.name)
            setTextViewText(R.id.episode_in_time, timeToGo)
            setTextViewText(R.id.episode_show, widgetEpisode.showName)
        }
    }
}

data class WidgetEpisode(val showName: String, val upcomingEpisode: UpcomingEpisode)