package com.redridgeapps.trakx.ui.episode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.EpisodeDetail
import com.redridgeapps.trakx.ui.common.dagger.AssistedViewModelFactory
import com.redridgeapps.trakx.ui.common.viewModelArgs
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

class EpisodeViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val tmDbService: TMDbService
) : ViewModel() {

    private val args by viewModelArgs<EpisodeViewModelArgs>(handle)
    private val _episodeDetailLiveData = MutableLiveData<EpisodeDetail>()
    val episodeDetailLiveData: LiveData<EpisodeDetail> = _episodeDetailLiveData

    init {
        fetchEpisodeDetail()
    }

    private fun fetchEpisodeDetail() = viewModelScope.launch {
        val episodeDetail = tmDbService.getEpisodeDetail(
            args.tvShowId,
            args.seasonNumber,
            args.episodeNumber
        )
        _episodeDetailLiveData.setValue(episodeDetail)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory
}
