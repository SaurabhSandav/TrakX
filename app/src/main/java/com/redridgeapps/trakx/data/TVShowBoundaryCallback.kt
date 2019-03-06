package com.redridgeapps.trakx.data

import androidx.paging.PagedList
import com.redridgeapps.trakx.CachedCollectionQueries
import com.redridgeapps.trakx.CachedShowQueries
import com.redridgeapps.trakx.InMemoryCacheDatabase
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.model.tmdb.TVShowCollection
import com.redridgeapps.trakx.utils.Constants.RequestType
import com.redridgeapps.trakx.utils.Constants.RequestType.AIRING_TODAY
import com.redridgeapps.trakx.utils.Constants.RequestType.ON_THE_AIR
import com.redridgeapps.trakx.utils.Constants.RequestType.POPULAR
import com.redridgeapps.trakx.utils.Constants.RequestType.TOP_RATED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class TVShowBoundaryCallback(
    tmDbService: TMDbService,
    private val inMemoryCacheDatabase: InMemoryCacheDatabase,
    override val coroutineContext: CoroutineContext,
    private val requestType: RequestType
) : PagedList.BoundaryCallback<TVShow>(), CoroutineScope {

    private val cachedCollectionQueries = inMemoryCacheDatabase.cachedCollectionQueries
    private val request: (Int) -> Deferred<TVShowCollection>
    private var position = 1
    private var lastPage: Int? = null

    init {
        request = buildRequest(tmDbService)
    }

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        launch { fetchTVShows() }
    }

    override fun onItemAtEndLoaded(itemAtEnd: TVShow) {
        super.onItemAtEndLoaded(itemAtEnd)
        launch { fetchTVShows() }
    }

    private fun buildRequest(tmDbService: TMDbService): (Int) -> Deferred<TVShowCollection> {
        return when (requestType) {
            POPULAR -> { page -> tmDbService.getPopular(page) }
            TOP_RATED -> { page -> tmDbService.getTopRated(page) }
            ON_THE_AIR -> { page -> tmDbService.getOnTheAir(page) }
            AIRING_TODAY -> { page -> tmDbService.getAiringToday(page) }
            else -> throw IllegalArgumentException()
        }
    }

    private suspend fun fetchTVShows() {

        val previousPage = lastPage ?: withContext(Dispatchers.IO) {
            cachedCollectionQueries.getLastPage(requestType.name).executeAsOneOrNull()
        }
        val newPage = if (previousPage != null) previousPage + 1 else 1

        val tvShowList = request(newPage).await().results
        val cachedCollection = tvShowList.map {
            CachedCategory(
                showId = it.id,
                position = position++,
                page = newPage,
                cacheCategory = requestType.name
            )
        }

        lastPage = newPage

        inMemoryCacheDatabase.cacheCategory(tvShowList, cachedCollection)
    }

    private suspend fun InMemoryCacheDatabase.cacheCategory(
        tvShowList: List<TVShow>,
        cachedCollection: List<CachedCategory>
    ) = withContext(Dispatchers.IO) {
        transaction {
            cachedShowQueries.cacheShowToDB(tvShowList)
            cachedCollectionQueries.cacheCollectionToDB(cachedCollection)
        }
    }

    private fun CachedShowQueries.cacheShowToDB(tvShowList: List<TVShow>) {
        tvShowList.forEach {
            insert(
                id = it.id,
                originalName = it.originalName,
                name = it.name,
                popularity = it.popularity,
                firstAirDate = it.firstAirDate,
                backdropPath = it.backdropPath,
                overview = it.overview,
                posterPath = it.posterPath,
                voteAverage = it.voteAverage
            )
        }
    }

    private fun CachedCollectionQueries.cacheCollectionToDB(cachedCollection: List<CachedCategory>) {
        cachedCollection.forEach {
            insert(showId = it.showId, position = it.position, page = it.page, cacheCategory = it.cacheCategory)
        }
    }
}

private data class CachedCategory(val showId: Int, val position: Int, val page: Int, val cacheCategory: String)