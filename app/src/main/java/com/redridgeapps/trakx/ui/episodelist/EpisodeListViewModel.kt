package com.redridgeapps.trakx.ui.episodelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.SeasonDetail
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpisodeListViewModel @Inject constructor(
    private val tmDbService: TMDbService
) : ViewModel() {

    private val _seasonDetailLiveData = MutableLiveData<SeasonDetail>()
    val seasonDetailLiveData: LiveData<SeasonDetail> = _seasonDetailLiveData

    fun setTVSeason(tvShowId: Int, seasonNumber: Int) {
        fetchSeasonDetail(tvShowId, seasonNumber)
    }

    private fun fetchSeasonDetail(tvShowId: Int, seasonNumber: Int) = viewModelScope.launch {
        val seasonDetail = tmDbService.getSeasonDetail(tvShowId, seasonNumber)
        _seasonDetailLiveData.setValue(seasonDetail)
    }
}
