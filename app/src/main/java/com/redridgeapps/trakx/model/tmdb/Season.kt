package com.redridgeapps.trakx.model.tmdb

import com.redridgeapps.trakx.utils.moshi.LongDate
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Season(

    @Json(name = "id")
    val id: Int,

    @LongDate
    @Json(name = "air_date")
    val airDate: Long,

    @Json(name = "episode_count")
    val episodeCount: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "overview")
    val overview: String,

    @Json(name = "poster_path")
    val posterPath: String?,

    @Json(name = "season_number")
    val seasonNumber: Int
)
