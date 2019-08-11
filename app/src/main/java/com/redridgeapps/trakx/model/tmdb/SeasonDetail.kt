package com.redridgeapps.trakx.model.tmdb

import com.redridgeapps.trakx.utils.moshi.LongDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonDetail(

    @SerialName("id")
    val id: Int,

    @SerialName("_id")
    val _id: String,

    @Serializable(with = LongDateSerializer::class)
    @SerialName("air_date")
    val airDate: Long,

    @SerialName("episodes")
    val episodes: List<Episode>,

    @SerialName("name")
    val name: String,

    @SerialName("overview")
    val overview: String?,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("season_number")
    val seasonNumber: Int
)
