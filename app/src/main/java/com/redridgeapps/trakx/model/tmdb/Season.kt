package com.redridgeapps.trakx.model.tmdb

import com.redridgeapps.trakx.model.EventDate
import com.redridgeapps.trakx.utils.serialization.LongDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Season(

    @SerialName("id")
    val id: Int,

    @Serializable(with = LongDateSerializer::class)
    @SerialName("air_date")
    val airDate: Long,

    @SerialName("episode_count")
    val episodeCount: Int,

    @SerialName("name")
    val name: String,

    @SerialName("overview")
    val overview: String?,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("season_number")
    val seasonNumber: Int
) {

    @Transient
    val airEventDate: EventDate = EventDate(airDate)
}
