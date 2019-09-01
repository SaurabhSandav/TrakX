package com.redridgeapps.trakx.ui.detail

import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.ui.common.ViewModelArgs
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailViewModelArgs(
    val tvShow: TVShow
) : ViewModelArgs
