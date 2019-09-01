package com.redridgeapps.trakx.ui.episodelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.SeasonDetail
import com.redridgeapps.trakx.ui.common.dagger.AssistedViewModelFactory
import com.redridgeapps.trakx.ui.common.viewModelArgs
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class EpisodeListViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val tmDbService: TMDbService
) : ViewModel() {

    private val args by viewModelArgs<EpisodeListViewModelArgs>(handle)

    val seasonDetailLiveData: LiveData<SeasonDetail> = liveData {
        val seasonDetail = tmDbService.getSeasonDetail(args.tvShowId, args.seasonNumber)
        emit(seasonDetail)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory
}
