package com.redridgeapps.trakx.data

import androidx.paging.PagedList
import com.redridgeapps.trakx.CachedCollectionQueries
import com.redridgeapps.trakx.CachedShowQueries
import com.redridgeapps.trakx.Database
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.model.local.CachedCategory
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
    private val database: Database,
    override val coroutineContext: CoroutineContext,
    private val requestType: RequestType
) : PagedList.BoundaryCallback<TVShow>(), CoroutineScope {

    private val cachedCollectionQueries = database.cachedCollectionQueries
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
        val cachedCategoryList = tvShowList.map { CachedCategory(it.id, newPage, position++, requestType.name) }

        lastPage = newPage

        database.cacheCategory(tvShowList, cachedCategoryList)
    }

    private suspend fun Database.cacheCategory(
        tvShowList: List<TVShow>,
        cachedCategoryList: List<CachedCategory>
    ) = withContext(Dispatchers.IO) {
        transaction {
            cachedShowQueries.cacheShowToDB(tvShowList)
            cachedCollectionQueries.cacheCollectionToDB(cachedCategoryList)
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

    private fun CachedCollectionQueries.cacheCollectionToDB(cachedCategoryList: List<CachedCategory>) {
        cachedCategoryList.forEach {
            insert(showId = it.showId, position = it.position, page = it.page, cacheCategory = it.cacheCategory)
        }
    }
}
