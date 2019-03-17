package com.redridgeapps.trakx.db.mapper

import com.redridgeapps.trakx.CachedShow
import com.redridgeapps.trakx.model.tmdb.TVShow

fun TVShow.toCachedShow(): CachedShow = CachedShow.Impl(
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
