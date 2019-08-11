package com.redridgeapps.trakx.model.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TVShowDetail(

    @SerialName("id")
    val id: Int,

    @SerialName("backdrop_path")
    val backdropPath: String?,

    @SerialName("name")
    val name: String,

    @SerialName("number_of_episodes")
    val numberOfEpisodes: Int,

    @SerialName("number_of_seasons")
    val numberOfSeasons: Int,

    @SerialName("original_name")
    val originalName: String,

    @SerialName("overview")
    val overview: String,

    @SerialName("popularity")
    val popularity: Float,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("seasons")
    val seasons: List<Season>,

    @SerialName("status")
    val status: String,

    @SerialName("vote_average")
    val voteAverage: Float
)
