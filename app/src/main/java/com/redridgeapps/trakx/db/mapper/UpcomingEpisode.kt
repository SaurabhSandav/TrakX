package com.redridgeapps.trakx.db.mapper

import com.redridgeapps.trakx.UpcomingEpisode
import com.redridgeapps.trakx.model.tmdb.Episode

fun Episode.toUpcomingEpisode(): UpcomingEpisode = UpcomingEpisode.Impl(
    id = id,
    airDate = airDate,
    episodeNumber = episodeNumber,
    name = name,
    overview = overview,
    seasonNumber = seasonNumber,
    showId = showId,
    stillPath = stillPath,
    voteAverage = voteAverage
)