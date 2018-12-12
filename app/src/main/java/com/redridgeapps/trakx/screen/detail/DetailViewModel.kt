package com.redridgeapps.trakx.screen.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.AppDatabase
import com.redridgeapps.trakx.db.TrackedShowDao
import com.redridgeapps.trakx.db.UpcomingEpisodeDao
import com.redridgeapps.trakx.model.local.TrackedShow
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
    database: AppDatabase
) : BaseViewModel() {

    private val trackedShowDao: TrackedShowDao = database.trackedShowDao()
    private val upcomingEpisodeDao: UpcomingEpisodeDao = database.upcomingEpisodeDao()
    private val _tvShowDetailLiveData = MutableLiveData<TVShowDetail>()
    private lateinit var tvShow: TVShow

    lateinit var isShowTrackedLiveData: LiveData<Boolean>
    lateinit var upcomingEpisodeUpdatedLiveData: LiveData<Unit>
    val tvShowDetailLiveData: LiveData<TVShowDetail> = _tvShowDetailLiveData

    fun setTVShow(newTVShow: TVShow) {
        tvShow = newTVShow

        isShowTrackedLiveData = Transformations.map(trackedShowDao.getShow(newTVShow.id)) { it.isNotEmpty() }!!
        upcomingEpisodeUpdatedLiveData =
                Transformations.map(upcomingEpisodeDao.observeOfShowID(newTVShow.id)) { Unit }!!
        fetchTVShowDetail()
    }

    fun trackShow(enableTracking: Boolean) = launch {
        val trackedShow = TrackedShow(showId = tvShow.id)

        withContext(Dispatchers.IO) {
            if (enableTracking) trackedShowDao.insert(trackedShow)
            else trackedShowDao.deleteOfShowID(tvShow.id)
        }

        fetchUpcomingEpisodes(enableTracking)
    }

    private fun fetchTVShowDetail() = launch {
        val tvShowDetail = tmDbService.getTVDetail(tvShow.id).await()
        _tvShowDetailLiveData.setValue(tvShowDetail)
    }

    private suspend fun fetchUpcomingEpisodes(enableTracking: Boolean) {
        if (!enableTracking || tvShowDetailLiveData.value == null) {
            withContext(Dispatchers.IO) { upcomingEpisodeDao.deleteOfShowID(tvShow.id) }
        } else {
            val lastSeason = tvShowDetailLiveData.value!!.seasons.last()
            val seasonDetail = tmDbService.getSeasonDetail(tvShow.id, lastSeason.seasonNumber).await()

            val todayCalendar = DateTimeUtils.todayDateInstance
            val airCalendar = Calendar.getInstance()

            val upcomingEpisodes = seasonDetail.episodes.filter {
                airCalendar.timeInMillis = it.airDate
                DateTimeUtils.isUpcoming(todayCalendar, airCalendar)
            }

            withContext(Dispatchers.IO) { upcomingEpisodeDao.deleteAndInsertOfShow(tvShow, upcomingEpisodes) }
        }
    }
}
