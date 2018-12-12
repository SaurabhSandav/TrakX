package com.redridgeapps.trakx.screen.episode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.EpisodeDetail
import com.redridgeapps.trakx.screen.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpisodeViewModel @Inject constructor(
    private val tmDbService: TMDbService
) : BaseViewModel() {

    private val _episodeDetailLiveData = MutableLiveData<EpisodeDetail>()
    val episodeDetailLiveData: LiveData<EpisodeDetail> = _episodeDetailLiveData

    fun setTVEpisode(tvShowId: Int, seasonNumber: Int, episodeNumber: Int) {
        fetchEpisodeDetail(tvShowId, seasonNumber, episodeNumber)
    }

    private fun fetchEpisodeDetail(tvShowId: Int, seasonNumber: Int, episodeNumber: Int) = launch {
        val episodeDetail = tmDbService.getEpisodeDetail(tvShowId, seasonNumber, episodeNumber).await()
        _episodeDetailLiveData.setValue(episodeDetail)
    }
}
