package com.redridgeapps.trakx.model.tmdb

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TVShowDetail(

    @Json(name = "id")
    val id: Int,

    @Json(name = "backdrop_path")
    val backdropPath: String?,

    @Json(name = "name")
    val name: String,

    @Json(name = "number_of_episodes")
    val numberOfEpisodes: Int,

    @Json(name = "number_of_seasons")
    val numberOfSeasons: Int,

    @Json(name = "original_name")
    val originalName: String,

    @Json(name = "overview")
    val overview: String,

    @Json(name = "popularity")
    val popularity: Float,

    @Json(name = "poster_path")
    val posterPath: String?,

    @Json(name = "seasons")
    val seasons: List<Season>,

    @Json(name = "status")
    val status: String,

    @Json(name = "vote_average")
    val voteAverage: Float
)
