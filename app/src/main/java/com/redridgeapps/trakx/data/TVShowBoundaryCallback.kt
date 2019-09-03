package com.redridgeapps.trakx.data

import androidx.paging.PagedList
import com.redridgeapps.trakx.CacheDB
import com.redridgeapps.trakx.CachedCollection
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.mapper.toCachedShow
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.model.tmdb.TVShowCollection
import com.redridgeapps.trakx.utils.Constants.RequestType
import com.redridgeapps.trakx.utils.Constants.RequestType.AIRING_TODAY
import com.redridgeapps.trakx.utils.Constants.RequestType.ON_THE_AIR
import com.redridgeapps.trakx.utils.Constants.RequestType.POPULAR
import com.redridgeapps.trakx.utils.Constants.RequestType.TOP_RATED
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TVShowBoundaryCallback(
    private val tmDbService: TMDbService,
    private val cacheDB: CacheDB,
    private val requestType: RequestType,
    coroutineScope: CoroutineScope
) : PagedList.BoundaryCallback<TVShow>(), CoroutineScope by coroutineScope {

    private val cachedCollectionQueries = cacheDB.cachedCollectionQueries
    private val request: suspend (Int) -> TVShowCollection

    init {
        request = buildRequest()
    }

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        launch { fetchTVShows() }
    }

    override fun onItemAtEndLoaded(itemAtEnd: TVShow) {
        super.onItemAtEndLoaded(itemAtEnd)
        launch { fetchTVShows() }
    }

    private fun buildRequest(): suspend (Int) -> TVShowCollection {
        return when (requestType) {
            POPULAR -> { page -> tmDbService.getPopular(page) }
            TOP_RATED -> { page -> tmDbService.getTopRated(page) }
            ON_THE_AIR -> { page -> tmDbService.getOnTheAir(page) }
            AIRING_TODAY -> { page -> tmDbService.getAiringToday(page) }
            else -> error("Invalid Request type!")
        }
    }

    private suspend fun fetchTVShows() = withContext(Dispatchers.IO) {

        val lastRow = cachedCollectionQueries.getLastRow(requestType.name).executeAsOneOrNull()
        val page = lastRow?.page?.plus(1) ?: 1
        var position = lastRow?.position ?: 0

        val tvShowList = request(page).results
        val cachedCollection = tvShowList.map<TVShow, CachedCollection> {
            CachedCollection.Impl(
                showId = it.id,
                position = ++position,
                page = page,
                cacheCategory = requestType.name
            )
        }

        cacheDB.cacheCategory(tvShowList, cachedCollection)
    }

    private fun CacheDB.cacheCategory(
        tvShowList: List<TVShow>,
        cachedCollection: List<CachedCollection>
    ) = transaction {
        tvShowList.map(TVShow::toCachedShow).forEach(cachedShowQueries::insert)
        cachedCollection.forEach(cachedCollectionQueries::insert)
    }

    @Reusable
    class Factory @Inject constructor(
        private val tmDbService: TMDbService,
        private val cacheDB: CacheDB
    ) {

        fun create(
            coroutineScope: CoroutineScope,
            requestType: RequestType
        ) = TVShowBoundaryCallback(tmDbService, cacheDB, requestType, coroutineScope)
    }
}
