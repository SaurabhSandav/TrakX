package com.redridgeapps.trakx.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.redridgeapps.trakx.AppDatabase
import com.redridgeapps.trakx.UpcomingEpisodesQueries
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.mapper.toTrackedShow
import com.redridgeapps.trakx.db.mapper.toUpcomingEpisode
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.model.tmdb.TVShowDetail
import com.redridgeapps.trakx.ui.base.BaseViewModel
import com.redridgeapps.trakx.utils.DateTimeUtils
import com.squareup.sqldelight.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val tmDbService: TMDbService,
    appDatabase: AppDatabase
) : BaseViewModel() {

    private val trackedShowQueries = appDatabase.trackedShowQueries
    private val upcomingEpisodesQueries = appDatabase.upcomingEpisodesQueries
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
            else trackedShowQueries.insert(tvShow.toTrackedShow())
        }

        fetchUpcomingEpisodes(enableTracking)
    }

    private suspend fun isShowTracked() = withContext(Dispatchers.IO) {
        val query = trackedShowQueries.getShow(tvShow.id)

        query.addListener(object : Query.Listener {
            override fun queryResultsChanged() {
                _isShowTrackedLiveData.postValue(query.executeAsList().isNotEmpty())
            }
        })

        query.notifyDataChanged()
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

    private fun UpcomingEpisodesQueries.clearAndInsertOfShowToDB(episodes: List<Episode>) = transaction {
        deleteOfShowID(tvShow.id)
        episodes.map(Episode::toUpcomingEpisode).forEach { insert(it) }
    }
}
