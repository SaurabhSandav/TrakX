package com.redridgeapps.trakx.ui.episode

import com.redridgeapps.trakx.ui.common.ViewModelArgs
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EpisodeViewModelArgs(
    val tvShowId: Int,
    val seasonNumber: Int,
    val episodeNumber: Int
) : ViewModelArgs
