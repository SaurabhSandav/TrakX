package com.redridgeapps.trakx.model.tmdb

import com.redridgeapps.trakx.utils.moshi.LongDate
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SeasonDetail(

    @Json(name = "id")
    val id: Int,

    @Json(name = "_id")
    val _id: String,

    @LongDate
    @Json(name = "air_date")
    val airDate: Long,

    @Json(name = "episodes")
    val episodes: List<Episode>,

    @Json(name = "name")
    val name: String,

    @Json(name = "overview")
    val overview: String,

    @Json(name = "poster_path")
    val posterPath: String?,

    @Json(name = "season_number")
    val seasonNumber: Int
)
