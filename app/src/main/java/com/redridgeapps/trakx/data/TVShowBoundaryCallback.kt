package com.redridgeapps.trakx.data

import androidx.paging.PagedList
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.AppDatabase
import com.redridgeapps.trakx.db.CachedCategoryDao
import com.redridgeapps.trakx.db.ShowCacheDao
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
    private val appDatabase: AppDatabase,
    override val coroutineContext: CoroutineContext,
    private val requestType: RequestType
) : PagedList.BoundaryCallback<TVShow>(), CoroutineScope {

    private val showCacheDao: ShowCacheDao = appDatabase.showCacheDao()
    private val cachedCategoryDao: CachedCategoryDao = appDatabase.cachedCategoryDao()
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

        val previousPage = lastPage ?: cachedCategoryDao.getLastPage(requestType.name)
        val newPage = if (previousPage != null) previousPage + 1 else 1

        val tvShowList = request(newPage).await().results
        val cachedCategoryList = tvShowList.map { CachedCategory(it.id, newPage, position++, requestType.name) }

        lastPage = newPage

        withContext(Dispatchers.IO) {
            appDatabase.runInTransaction {
                cachedCategoryDao.insertList(cachedCategoryList)
                showCacheDao.insertList(tvShowList)
            }
        }
    }
}
