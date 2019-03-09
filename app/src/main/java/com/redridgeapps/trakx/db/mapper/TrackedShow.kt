package com.redridgeapps.trakx.db.mapper

import com.redridgeapps.trakx.TrackedShow
import com.redridgeapps.trakx.model.tmdb.TVShow

fun TrackedShow.toTVShow() = TVShow(
    id = id,
    originalName = originalName,
    name = name,
    popularity = popularity,
    firstAirDate = firstAirDate,
    backdropPath = backdropPath,
    overview = overview,
    posterPath = posterPath,
    voteAverage = voteAverage
)

fun TVShow.toTrackedShow(): TrackedShow = TrackedShow.Impl(
    id = id,
    originalName = originalName,
    name = name,
    popularity = popularity,
    firstAirDate = firstAirDate,
    backdropPath = backdropPath,
    overview = overview,
    posterPath = posterPath,
    voteAverage = voteAverage
)