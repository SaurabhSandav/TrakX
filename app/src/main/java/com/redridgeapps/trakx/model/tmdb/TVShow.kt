package com.redridgeapps.trakx.model.tmdb

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.redridgeapps.trakx.utils.moshi.LongDate
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
@JsonClass(generateAdapter = true)
data class TVShow(

    @PrimaryKey
    @Json(name = "id")
    val id: Int,

    @Json(name = "original_name")
    val originalName: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "popularity")
    val popularity: Float,

    @LongDate
    @Json(name = "first_air_date")
    val firstAirDate: Long,

    @Json(name = "backdrop_path")
    val backdropPath: String?,

    @Json(name = "overview")
    val overview: String,

    @Json(name = "poster_path")
    val posterPath: String?,

    @Json(name = "vote_average")
    val voteAverage: Float
) : Parcelable {

    object DiffCallback : DiffUtil.ItemCallback<TVShow>() {

        override fun areItemsTheSame(oldItem: TVShow, newItem: TVShow) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TVShow, newItem: TVShow) = true
    }
}
