package com.redridgeapps.trakx.ui.episode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.EpisodeDetail
import com.redridgeapps.trakx.ui.common.dagger.AssistedViewModelFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

class EpisodeViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val tmDbService: TMDbService
) : ViewModel() {

    private val _episodeDetailLiveData = MutableLiveData<EpisodeDetail>()
    val episodeDetailLiveData: LiveData<EpisodeDetail> = _episodeDetailLiveData

    fun setTVEpisode(tvShowId: Int, seasonNumber: Int, episodeNumber: Int) {
        fetchEpisodeDetail(tvShowId, seasonNumber, episodeNumber)
    }

    private fun fetchEpisodeDetail(
        tvShowId: Int,
        seasonNumber: Int,
        episodeNumber: Int
    ) = viewModelScope.launch {
        val episodeDetail = tmDbService.getEpisodeDetail(tvShowId, seasonNumber, episodeNumber)
        _episodeDetailLiveData.setValue(episodeDetail)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory
}
