package com.redridgeapps.trakx.data

import androidx.paging.PagedList
import com.redridgeapps.trakx.CachedCollection
import com.redridgeapps.trakx.InMemoryCacheDatabase
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.mapper.toCachedShow
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.model.tmdb.TVShowCollection
import com.redridgeapps.trakx.utils.Constants.RequestType
import com.redridgeapps.trakx.utils.Constants.RequestType.AIRING_TODAY
import com.redridgeapps.trakx.utils.Constants.RequestType.ON_THE_AIR
import com.redridgeapps.trakx.utils.Constants.RequestType.POPULAR
import com.redridgeapps.trakx.utils.Constants.RequestType.TOP_RATED
import kotlinx.coroutines.CoroutineScope
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
    private val request: suspend (Int) -> TVShowCollection
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

    private fun buildRequest(tmDbService: TMDbService): suspend (Int) -> TVShowCollection {
        return when (requestType) {
            POPULAR -> { page -> tmDbService.getPopular(page) }
            TOP_RATED -> { page -> tmDbService.getTopRated(page) }
            ON_THE_AIR -> { page -> tmDbService.getOnTheAir(page) }
            AIRING_TODAY -> { page -> tmDbService.getAiringToday(page) }
            else -> error("Invalid Request type!")
        }
    }

    private suspend fun fetchTVShows() = withContext(Dispatchers.IO) {

        val previousPage = lastPage ?: cachedCollectionQueries.getLastPage(requestType.name).executeAsOneOrNull()
        val newPage = if (previousPage != null) previousPage + 1 else 1

        val tvShowList = request(newPage).results
        val cachedCollection = tvShowList.map<TVShow, CachedCollection> {
            CachedCollection.Impl(
                showId = it.id,
                position = position++,
                page = newPage,
                cacheCategory = requestType.name
            )
        }

        lastPage = newPage

        inMemoryCacheDatabase.cacheCategory(tvShowList, cachedCollection)
    }

    private fun InMemoryCacheDatabase.cacheCategory(
        tvShowList: List<TVShow>,
        cachedCollection: List<CachedCollection>
    ) = transaction {
        tvShowList.map(TVShow::toCachedShow).forEach(cachedShowQueries::insert)
        cachedCollection.forEach(cachedCollectionQueries::insert)
    }
}
