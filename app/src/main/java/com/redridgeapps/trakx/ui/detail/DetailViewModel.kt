package com.redridgeapps.trakx.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.trakx.AppDatabase
import com.redridgeapps.trakx.UpcomingEpisodesQueries
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.mapper.toTrackedShow
import com.redridgeapps.trakx.db.mapper.toUpcomingEpisode
import com.redridgeapps.trakx.model.isUpcoming
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.model.tmdb.TVShowDetail
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val tmDbService: TMDbService,
    appDatabase: AppDatabase
) : ViewModel() {

    private val trackedShowQueries = appDatabase.trackedShowQueries
    private val upcomingEpisodesQueries = appDatabase.upcomingEpisodesQueries
    private val _isShowTrackedLiveData = MutableLiveData<Boolean>()
    private val _tvShowDetailLiveData = MutableLiveData<TVShowDetail>()
    private lateinit var tvShow: TVShow

    val isShowTrackedLiveData: LiveData<Boolean> = _isShowTrackedLiveData
    val tvShowDetailLiveData: LiveData<TVShowDetail> = _tvShowDetailLiveData

    fun setTVShow(newTVShow: TVShow) {
        tvShow = newTVShow

        isShowTracked()
        fetchTVShowDetail()
    }

    fun trackShow(enableTracking: Boolean) = viewModelScope.launch {

        withContext(Dispatchers.IO) {
            if (!enableTracking) trackedShowQueries.deleteWithID(tvShow.id)
            else trackedShowQueries.insert(tvShow.toTrackedShow())
        }

        fetchUpcomingEpisodes(enableTracking)
    }

    private fun isShowTracked() {
        trackedShowQueries.getShow(tvShow.id)
            .asFlow()
            .mapToList()
            .onEach {
                _isShowTrackedLiveData.postValue(it.isNotEmpty())
            }
            .launchIn(viewModelScope)
    }

    private fun fetchTVShowDetail() = viewModelScope.launch {
        val tvShowDetail = tmDbService.getTVDetail(tvShow.id)
        _tvShowDetailLiveData.setValue(tvShowDetail)
    }

    private suspend fun fetchUpcomingEpisodes(
        enableTracking: Boolean
    ) = withContext(Dispatchers.IO) {
        if (!enableTracking || tvShowDetailLiveData.value == null) {
            upcomingEpisodesQueries.deleteOfShowID(tvShow.id)
        } else {
            val lastSeason = tvShowDetailLiveData.value!!.seasons.last()
            val seasonDetail = tmDbService.getSeasonDetail(tvShow.id, lastSeason.seasonNumber)
            val upcomingEpisodes = seasonDetail.episodes.filter { it.airEventDate.isUpcoming() }

            upcomingEpisodesQueries.clearAndInsertOfShowToDB(upcomingEpisodes)
        }
    }

    private fun UpcomingEpisodesQueries.clearAndInsertOfShowToDB(
        episodes: List<Episode>
    ) = transaction {
        deleteOfShowID(tvShow.id)
        episodes.map(Episode::toUpcomingEpisode).forEach { insert(it) }
    }
}
