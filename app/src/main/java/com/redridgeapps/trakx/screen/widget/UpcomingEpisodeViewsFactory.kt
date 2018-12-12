package com.redridgeapps.trakx.screen.widget

import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.db.AppDatabase
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.utils.DateTimeUtils
import kotlinx.coroutines.runBlocking

class UpcomingEpisodeViewsFactory(
    private val packageName: String,
    appDatabase: AppDatabase
) : RemoteViewsService.RemoteViewsFactory {

    private var widgetEpisodeList = mutableListOf<WidgetEpisode>()
    private val upcomingEpisodeDao = appDatabase.upcomingEpisodeDao()
    private val trackedShowDao = appDatabase.trackedShowDao()

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
        runBlocking {

            val episodeList = upcomingEpisodeDao.upcomingEpisodes()

            widgetEpisodeList.clear()

            episodeList.mapTo(widgetEpisodeList) {
                val trackedShow = trackedShowDao.getShowSingle(it.showId)
                WidgetEpisode(trackedShow.name, it)
            }
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

data class WidgetEpisode(val showName: String, val upcomingEpisode: Episode)