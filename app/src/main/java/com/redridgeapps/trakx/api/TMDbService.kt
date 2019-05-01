package com.redridgeapps.trakx.api

import com.redridgeapps.trakx.BuildConfig
import com.redridgeapps.trakx.model.tmdb.EpisodeDetail
import com.redridgeapps.trakx.model.tmdb.SeasonDetail
import com.redridgeapps.trakx.model.tmdb.TVShowCollection
import com.redridgeapps.trakx.model.tmdb.TVShowDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDbService {

    @GET("tv/top_rated")
    suspend fun getTopRated(
        @Query("page") page: Int
    ): TVShowCollection

    @GET("tv/popular")
    suspend fun getPopular(
        @Query("page") page: Int
    ): TVShowCollection

    @GET("tv/on_the_air")
    suspend fun getOnTheAir(
        @Query("page") page: Int
    ): TVShowCollection

    @GET("tv/airing_today")
    suspend fun getAiringToday(
        @Query("page") page: Int
    ): TVShowCollection

    @GET("tv/{tvId}")
    suspend fun getTVDetail(
        @Path("tvId") tvId: Int
    ): TVShowDetail

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getSeasonDetail(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int
    ): SeasonDetail

    @GET("tv/{tv_id}/season/{season_number}/episode/{episode_number}")
    suspend fun getEpisodeDetail(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int
    ): EpisodeDetail

    companion object {

        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val TMDB_API_KEY = BuildConfig.TMDB_API_KEY

        private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
        private const val DEFAULT_IMAGE_POSTER_SIZE = "w342"
        private const val DEFAULT_IMAGE_BACKDROP_SIZE = "w780"

        fun buildPosterURL(path: String) = IMAGE_BASE_URL + DEFAULT_IMAGE_POSTER_SIZE + path

        fun buildBackdropURL(path: String) = IMAGE_BASE_URL + DEFAULT_IMAGE_BACKDROP_SIZE + path
    }
}
