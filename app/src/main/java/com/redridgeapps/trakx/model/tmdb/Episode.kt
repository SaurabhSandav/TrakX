package com.redridgeapps.trakx.model.tmdb

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.redridgeapps.trakx.utils.moshi.LongDate
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "UpcomingEpisode")
data class Episode(

    @PrimaryKey
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

    @Json(name = "show_id")
    val showId: Int,

    @Json(name = "still_path")
    val stillPath: String?,

    @Json(name = "vote_average")
    val voteAverage: Double
) {

    object DiffCallback : DiffUtil.ItemCallback<Episode>() {

        override fun areItemsTheSame(oldItem: Episode, newItem: Episode) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode) = true
    }
}
