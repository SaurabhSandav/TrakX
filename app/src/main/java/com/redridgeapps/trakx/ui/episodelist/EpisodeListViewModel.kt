package com.redridgeapps.trakx.ui.episodelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.SeasonDetail
import com.redridgeapps.trakx.ui.common.dagger.AssistedViewModelFactory
import com.redridgeapps.trakx.ui.common.viewModelArgs
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

class EpisodeListViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val tmDbService: TMDbService
) : ViewModel() {

    private val args by viewModelArgs<EpisodeListViewModelArgs>(handle)
    private val _seasonDetailLiveData = MutableLiveData<SeasonDetail>()
    val seasonDetailLiveData: LiveData<SeasonDetail> = _seasonDetailLiveData

    init {
        fetchSeasonDetail()
    }

    private fun fetchSeasonDetail() = viewModelScope.launch {
        val seasonDetail = tmDbService.getSeasonDetail(args.tvShowId, args.seasonNumber)
        _seasonDetailLiveData.setValue(seasonDetail)
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory
}
