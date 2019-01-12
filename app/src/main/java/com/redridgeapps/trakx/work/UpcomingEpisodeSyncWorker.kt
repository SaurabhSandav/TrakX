package com.redridgeapps.trakx.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.redridgeapps.trakx.Database
import com.redridgeapps.trakx.UpcomingEpisodesQueries
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.utils.DateTimeUtils
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

class UpcomingEpisodeSyncWorker @Inject constructor(
    private val tmDbService: TMDbService,
    database: Database,
    appContext: Context,
    params: WorkerParameters
) : BaseWorker(appContext, params) {

    private val trackedShowQueries = database.trackedShowQueries
    private val upcomingEpisodesQueries = database.upcomingEpisodesQueries

    override suspend fun doWork(): Result {

        val trackedShows = trackedShowQueries.trackedShows().executeAsList()

        val upcomingEpisodes = trackedShows
            .map { tmDbService.getTVDetail(it.id).await() }
            .map { tmDbService.getSeasonDetail(it.id, it.seasons.last().seasonNumber).await() }
            .flatMap { it.episodes }
            .filterUpcoming()

        upcomingEpisodesQueries.clearAndInsertToDB(upcomingEpisodes)

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

    private fun UpcomingEpisodesQueries.clearAndInsertToDB(upcomingEpisodes: List<Episode>) = transaction {

        deleteAll()

        upcomingEpisodes.forEach {
            insert(
                id = it.id,
                airDate = it.airDate,
                episodeNumber = it.episodeNumber,
                name = it.name,
                overview = it.overview,
                seasonNumber = it.seasonNumber,
                showId = it.showId,
                stillPath = it.stillPath,
                voteAverage = it.voteAverage
            )
        }
    }

    @Singleton
    class Factory @Inject constructor(
        private val tmDbService: TMDbService,
        private val database: Database
    ) : BaseWorker.Factory {

        override fun create(appContext: Context, params: WorkerParameters): UpcomingEpisodeSyncWorker {
            return UpcomingEpisodeSyncWorker(tmDbService, database, appContext, params)
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