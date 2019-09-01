package com.redridgeapps.trakx.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.trakx.AppDatabase
import com.redridgeapps.trakx.UpcomingEpisodesQueries
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.mapper.toTrackedShow
import com.redridgeapps.trakx.db.mapper.toUpcomingEpisode
import com.redridgeapps.trakx.model.isUpcoming
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.model.tmdb.TVShowDetail
import com.redridgeapps.trakx.ui.common.dagger.AssistedViewModelFactory
import com.redridgeapps.trakx.ui.common.viewModelArgs
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val tmDbService: TMDbService,
    appDatabase: AppDatabase
) : ViewModel() {

    private val args by viewModelArgs<DetailViewModelArgs>(handle)
    private val trackedShowQueries = appDatabase.trackedShowQueries
    private val upcomingEpisodesQueries = appDatabase.upcomingEpisodesQueries
    private val _isShowTrackedLiveData = MutableLiveData<Boolean>()
    private val _tvShowDetailLiveData = MutableLiveData<TVShowDetail>()

    val isShowTrackedLiveData: LiveData<Boolean> = _isShowTrackedLiveData
    val tvShowDetailLiveData: LiveData<TVShowDetail> = _tvShowDetailLiveData

    init {
        isShowTracked()
        fetchTVShowDetail()
    }

    fun trackShow(enableTracking: Boolean) = viewModelScope.launch {

        withContext(Dispatchers.IO) {
            if (!enableTracking) trackedShowQueries.deleteWithID(args.tvShow.id)
            else trackedShowQueries.insert(args.tvShow.toTrackedShow())
        }

        fetchUpcomingEpisodes(enableTracking)
    }

    private fun isShowTracked() {
        trackedShowQueries.getShow(args.tvShow.id)
            .asFlow()
            .mapToList()
            .onEach {
                _isShowTrackedLiveData.postValue(it.isNotEmpty())
            }
            .launchIn(viewModelScope)
    }

    private fun fetchTVShowDetail() = viewModelScope.launch {
        val tvShowDetail = tmDbService.getTVDetail(args.tvShow.id)
        _tvShowDetailLiveData.setValue(tvShowDetail)
    }

    private suspend fun fetchUpcomingEpisodes(
        enableTracking: Boolean
    ) = withContext(Dispatchers.IO) {
        if (!enableTracking || tvShowDetailLiveData.value == null) {
            upcomingEpisodesQueries.deleteOfShowID(args.tvShow.id)
        } else {
            val lastSeason = tvShowDetailLiveData.value!!.seasons.last()
            val seasonDetail = tmDbService.getSeasonDetail(args.tvShow.id, lastSeason.seasonNumber)
            val upcomingEpisodes = seasonDetail.episodes.filter { it.airEventDate.isUpcoming() }

            upcomingEpisodesQueries.clearAndInsertOfShowToDB(upcomingEpisodes)
        }
    }

    private fun UpcomingEpisodesQueries.clearAndInsertOfShowToDB(
        episodes: List<Episode>
    ) = transaction {
        deleteOfShowID(args.tvShow.id)
        episodes.map(Episode::toUpcomingEpisode).forEach { insert(it) }
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory
}
