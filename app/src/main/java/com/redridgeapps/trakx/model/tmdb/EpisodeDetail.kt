package com.redridgeapps.trakx.model.tmdb

import com.redridgeapps.trakx.utils.moshi.LongDate
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EpisodeDetail(

    @Json(name = "id")
    val id: Int,

    @LongDate
    @Json(name = "air_date")
    val airDate: Long,

    @Json(name = "episode_number")
    val episodeNumber: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "overview")
    val overview: String,

    @Json(name = "season_number")
    val seasonNumber: Int,

    @Json(name = "still_path")
    val stillPath: String?,

    @Json(name = "vote_average")
    val voteAverage: Float
)
