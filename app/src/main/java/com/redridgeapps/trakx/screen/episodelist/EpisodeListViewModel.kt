package com.redridgeapps.trakx.screen.episodelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.SeasonDetail
import com.redridgeapps.trakx.screen.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpisodeListViewModel @Inject constructor(
    private val tmDbService: TMDbService
) : BaseViewModel() {

    private val _seasonDetailLiveData = MutableLiveData<SeasonDetail>()
    val seasonDetailLiveData: LiveData<SeasonDetail> = _seasonDetailLiveData

    fun setTVSeason(tvShowId: Int, seasonNumber: Int) {
        fetchSeasonDetail(tvShowId, seasonNumber)
    }

    private fun fetchSeasonDetail(tvShowId: Int, seasonNumber: Int) = launch {
        val seasonDetail = tmDbService.getSeasonDetail(tvShowId, seasonNumber).await()
        _seasonDetailLiveData.setValue(seasonDetail)
    }
}
