package com.redridgeapps.trakx.model.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TVShowCollection(

    @SerialName("page")
    val page: Int,

    @SerialName("total_results")
    val totalResults: Int,

    @SerialName("total_pages")
    val totalPages: Int,

    @SerialName("results")
    val results: List<TVShow>
)
