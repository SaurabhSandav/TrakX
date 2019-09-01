package com.redridgeapps.trakx.ui.episode

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.EpisodeDetail
import com.redridgeapps.trakx.ui.common.dagger.AssistedViewModelFactory
import com.redridgeapps.trakx.ui.common.viewModelArgs
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class EpisodeViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val tmDbService: TMDbService
) : ViewModel() {

    private val args by viewModelArgs<EpisodeViewModelArgs>(handle)

    val episodeDetailLiveData: LiveData<EpisodeDetail> = liveData {
        val episodeDetail = tmDbService.getEpisodeDetail(
            args.tvShowId,
            args.seasonNumber,
            args.episodeNumber
        )
        emit(episodeDetail)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory
}
