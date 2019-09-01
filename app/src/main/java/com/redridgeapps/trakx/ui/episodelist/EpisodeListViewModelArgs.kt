package com.redridgeapps.trakx.ui.episodelist

import com.redridgeapps.trakx.ui.common.ViewModelArgs
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EpisodeListViewModelArgs(
    val tvShowId: Int,
    val seasonNumber: Int
) : ViewModelArgs
