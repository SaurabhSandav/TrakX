package com.redridgeapps.trakx.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.redridgeapps.trakx.AppDatabase
import com.redridgeapps.trakx.UpcomingEpisodesQueries
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.mapper.toUpcomingEpisode
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

class UpcomingEpisodeSyncWorker @Inject constructor(
    private val tmDbService: TMDbService,
    appDatabase: AppDatabase,
    appContext: Context,
    params: WorkerParameters
) : BaseWorker(appContext, params) {

    private val trackedShowQueries = appDatabase.trackedShowQueries
    private val upcomingEpisodesQueries = appDatabase.upcomingEpisodesQueries

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val trackedShows = trackedShowQueries.trackedShows().executeAsList()

        val upcomingEpisodes = trackedShows
            .map { tmDbService.getTVDetail(it.id) }
            .map { tmDbService.getSeasonDetail(it.id, it.seasons.last().seasonNumber) }
            .flatMap { it.episodes }
            .filterUpcoming()

        upcomingEpisodesQueries.clearAndInsertToDB(upcomingEpisodes)

        return@withContext Result.success()
    }

    private fun List<Episode>.filterUpcoming(): List<Episode> {
        val todayCalendar = DateTimeUtils.todayDateInstance
        val airCalendar = Calendar.getInstance()

        return filter {
            airCalendar.timeInMillis = it.airDate
            DateTimeUtils.isUpcoming(todayCalendar, airCalendar)
        }
    }

    private fun UpcomingEpisodesQueries.clearAndInsertToDB(upcomingEpisodes: List<Episode>) = transaction {
        deleteAll()
        upcomingEpisodes.map(Episode::toUpcomingEpisode).forEach { insert(it) }
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

        fun scheduleDaily(appContext: Context) {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()

            val upcomingEpisodeSyncWork = PeriodicWorkRequestBuilder<UpcomingEpisodeSyncWorker>(
                3,
                TimeUnit.DAYS
            ).setConstraints(constraints).build()

            WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
                TAG_COMPLETE_SYNC,
                ExistingPeriodicWorkPolicy.KEEP,
                upcomingEpisodeSyncWork
            )
        }
    }
}
