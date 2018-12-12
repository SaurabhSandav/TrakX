package com.redridgeapps.trakx.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.AppDatabase
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.utils.DateTimeUtils
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

class UpcomingEpisodeSyncWorker @Inject constructor(
    private val tmDbService: TMDbService,
    private val appDatabase: AppDatabase,
    appContext: Context,
    params: WorkerParameters
) : BaseWorker(appContext, params) {

    override suspend fun doWork(): Result {

        val trackedShows = appDatabase.trackedShowDao().trackedShows()

        val upcomingEpisodes = trackedShows
            .map { tmDbService.getTVDetail(it.showId).await() }
            .map { tmDbService.getSeasonDetail(it.id, it.seasons.last().seasonNumber).await() }
            .flatMap { it.episodes }
            .filterUpcoming()

        appDatabase.upcomingEpisodeDao().deleteAllAndInsertOfShow(upcomingEpisodes)

        return Result.success()
    }

    private fun List<Episode>.filterUpcoming(): List<Episode> {
        val todayCalendar = DateTimeUtils.todayDateInstance
        val airCalendar = Calendar.getInstance()

        return filter {
            airCalendar.timeInMillis = it.airDate
            DateTimeUtils.isUpcoming(todayCalendar, airCalendar)
        }
    }

    @Singleton
    class Factory @Inject constructor(
        private val tmDbService: TMDbService,
        private val appDatabase: AppDatabase
    ) : BaseWorker.Factory {

        override fun create(appContext: Context, params: WorkerParameters): UpcomingEpisodeSyncWorker {
            return UpcomingEpisodeSyncWorker(tmDbService, appDatabase, appContext, params)
        }
    }

    companion object {

        private const val TAG_COMPLETE_SYNC = "TAG_COMPLETE_SYNC"

        fun scheduleDaily() {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()

            val upcomingEpisodeSyncWork = PeriodicWorkRequestBuilder<UpcomingEpisodeSyncWorker>(
                3,
                TimeUnit.DAYS
            ).setConstraints(constraints).build()

            WorkManager.getInstance().enqueueUniquePeriodicWork(
                TAG_COMPLETE_SYNC,
                ExistingPeriodicWorkPolicy.KEEP,
                upcomingEpisodeSyncWork
            )
        }
    }
}