package com.redridgeapps.trakx.model.tmdb

import androidx.recyclerview.widget.DiffUtil
import com.redridgeapps.trakx.utils.moshi.LongDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Episode(

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

    @SerialName("show_id")
    val showId: Int,

    @SerialName("still_path")
    val stillPath: String?,

    @SerialName("vote_average")
    val voteAverage: Double
) {

    object DiffCallback : DiffUtil.ItemCallback<Episode>() {

        override fun areItemsTheSame(oldItem: Episode, newItem: Episode) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode) = true
    }
}
