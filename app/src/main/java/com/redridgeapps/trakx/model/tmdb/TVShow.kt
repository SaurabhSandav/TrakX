package com.redridgeapps.trakx.model.tmdb

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.redridgeapps.trakx.utils.moshi.LongDateSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TVShow(

    @SerialName("id")
    val id: Int,

    @SerialName("original_name")
    val originalName: String,

    @SerialName("name")
    val name: String,

    @SerialName("popularity")
    val popularity: Float,

    @Serializable(with = LongDateSerializer::class)
    @SerialName("first_air_date")
    val firstAirDate: Long,

    @SerialName("backdrop_path")
    val backdropPath: String?,

    @SerialName("overview")
    val overview: String,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("vote_average")
    val voteAverage: Float
) : Parcelable {

    object DiffCallback : DiffUtil.ItemCallback<TVShow>() {

        override fun areItemsTheSame(oldItem: TVShow, newItem: TVShow) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TVShow, newItem: TVShow) = true
    }
}
