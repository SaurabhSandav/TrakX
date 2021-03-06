package com.redridgeapps.trakx.model.tmdb

import com.redridgeapps.trakx.utils.moshi.LongDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeDetail(

    @SerialName("id")
    val id: Int,

    @Serializable(with = LongDateSerializer::class)
    @SerialName("air_date")
    val airDate: Long,

    @SerialName("episode_number")
    val episodeNumber: Int,

    @SerialName("name")
    val name: String,

    @SerialName("overview")
    val overview: String,

    @SerialName("season_number")
    val seasonNumber: Int,

    @SerialName("still_path")
    val stillPath: String?,

    @SerialName("vote_average")
    val voteAverage: Float
)
