package com.redridgeapps.trakx.ui.tvshowlist

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.redridgeapps.trakx.Database
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.data.TVShowBoundaryCallback
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.ui.base.BaseViewModel
import com.redridgeapps.trakx.utils.Constants.RequestType
import com.redridgeapps.trakx.utils.Constants.RequestType.TRACKED
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TVShowListViewModel @Inject constructor(
    private val tmDbService: TMDbService,
    private val database: Database
) : BaseViewModel() {

    lateinit var tvShowPagedListLiveData: LiveData<PagedList<TVShow>>

    private lateinit var requestType: RequestType
    private val trackedShowQueries = database.trackedShowQueries
    private val cachedCollectionQueries = database.cachedCollectionQueries
    private val cachedShowQueries = database.cachedShowQueries
    private val config = Config(pageSize = PAGE_SIZE, initialLoadSizeHint = PAGE_SIZE * 2)

    private val shows: DataSource.Factory<Int, TVShow>
        get() = when (requestType) {
            TRACKED -> buildTrackedDataSource()
            else -> buildCachedCollectionDataSource()
        }

    init {
        launch { clearCache() }
    }

    fun setRequestType(newRequestType: RequestType) {
        if (this::requestType.isInitialized && newRequestType === requestType) return
        requestType = newRequestType

        val livePagedListBuilder = LivePagedListBuilder(shows, config)

        if (newRequestType !== TRACKED) {
            val boundaryCallback = TVShowBoundaryCallback(tmDbService, database, coroutineContext, newRequestType)

            livePagedListBuilder.setBoundaryCallback(boundaryCallback)
        }

        tvShowPagedListLiveData = livePagedListBuilder.build()
    }

    private fun buildTrackedDataSource(): DataSource.Factory<Int, TVShow> {
        return QueryDataSourceFactory(
            queryProvider = trackedShowQueries::trackedShowPaged,
            countQuery = trackedShowQueries.countTrackedShows()
        ).map {
            TVShow(
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

    private fun buildCachedCollectionDataSource(): DataSource.Factory<Int, TVShow> {
        return QueryDataSourceFactory(
            queryProvider = { limit, offset ->
                cachedCollectionQueries.cachedShowPaged(category = requestType.name, limit = limit, offset = offset)
            },
            countQuery = cachedCollectionQueries.countCachedShowPaged(requestType.name)
        ).map {
            TVShow(
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

    private suspend fun clearCache() = withContext(Dispatchers.IO) {
        cachedCollectionQueries.deleteAll()
        cachedShowQueries.deleteAll()
    }
}

private const val PAGE_SIZE = 20
