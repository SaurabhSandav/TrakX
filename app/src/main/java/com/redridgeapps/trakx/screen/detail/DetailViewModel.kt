package com.redridgeapps.trakx.screen.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.redridgeapps.trakx.Database
import com.redridgeapps.trakx.UpcomingEpisodesQueries
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.model.tmdb.TVShowDetail
import com.redridgeapps.trakx.screen.base.BaseViewModel
import com.redridgeapps.trakx.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val tmDbService: TMDbService,
    database: Database
) : BaseViewModel() {

    private val trackedShowQueries = database.trackedShowQueries
    private val upcomingEpisodesQueries = database.upcomingEpisodesQueries
    private val _isShowTrackedLiveData = MutableLiveData<Boolean>()
    private val _upcomingEpisodesUpdatedLiveData = MutableLiveData<Unit>()
    private val _tvShowDetailLiveData = MutableLiveData<TVShowDetail>()
    private lateinit var tvShow: TVShow

    val isShowTrackedLiveData: LiveData<Boolean> = _isShowTrackedLiveData
    val upcomingEpisodeUpdatedLiveData: LiveData<Unit> = _upcomingEpisodesUpdatedLiveData
    val tvShowDetailLiveData: LiveData<TVShowDetail> = _tvShowDetailLiveData

    fun setTVShow(newTVShow: TVShow) {
        tvShow = newTVShow

        launch { isShowTracked() }
        fetchTVShowDetail()
    }

    fun trackShow(enableTracking: Boolean) = launch {

        withContext(Dispatchers.IO) {
            if (!enableTracking) trackedShowQueries.deleteWithID(tvShow.id)
            else {
                trackedShowQueries.insert(
                    id = tvShow.id,
                    originalName = tvShow.originalName,
                    name = tvShow.name,
                    popularity = tvShow.popularity.toDouble(),
                    firstAirDate = tvShow.firstAirDate,
                    backdropPath = tvShow.backdropPath,
                    overview = tvShow.overview,
                    posterPath = tvShow.posterPath,
                    voteAverage = tvShow.voteAverage.toDouble()
                )
            }
        }

        _isShowTrackedLiveData.value = enableTracking
        fetchUpcomingEpisodes(enableTracking)
    }

    private suspend fun isShowTracked() = withContext(Dispatchers.IO) {
        val trackedShow = trackedShowQueries.getShow(tvShow.id).executeAsOneOrNull()
        _isShowTrackedLiveData.postValue(trackedShow != null)
    }

    private fun fetchTVShowDetail() = launch {
        val tvShowDetail = tmDbService.getTVDetail(tvShow.id).await()
        _tvShowDetailLiveData.setValue(tvShowDetail)
    }

    private suspend fun fetchUpcomingEpisodes(enableTracking: Boolean) = withContext(Dispatchers.IO) {
        if (!enableTracking || tvShowDetailLiveData.value == null) {
            upcomingEpisodesQueries.deleteOfShowID(tvShow.id)
        } else {
            val lastSeason = tvShowDetailLiveData.value!!.seasons.last()
            val seasonDetail = tmDbService.getSeasonDetail(tvShow.id, lastSeason.seasonNumber).await()

            val todayCalendar = DateTimeUtils.todayDateInstance
            val airCalendar = Calendar.getInstance()

            val upcomingEpisodes = seasonDetail.episodes.filter {
                airCalendar.timeInMillis = it.airDate
                DateTimeUtils.isUpcoming(todayCalendar, airCalendar)
            }

            upcomingEpisodesQueries.clearAndInsertOfShowToDB(upcomingEpisodes)
        }

        _upcomingEpisodesUpdatedLiveData.postValue(Unit)
    }

    private fun UpcomingEpisodesQueries.clearAndInsertOfShowToDB(upcomingEpisodes: List<Episode>) = transaction {

        deleteOfShowID(tvShow.id)

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
}
