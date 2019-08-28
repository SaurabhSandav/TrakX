package com.redridgeapps.trakx.ui.tvshowlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.redridgeapps.trakx.AppDatabase
import com.redridgeapps.trakx.InMemoryCacheDatabase
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.data.TVShowBoundaryCallback
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.utils.Constants.RequestType
import com.redridgeapps.trakx.utils.Constants.RequestType.TRACKED
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory
import javax.inject.Inject

class TVShowListViewModel @Inject constructor(
    private val tmDbService: TMDbService,
    private val inMemoryCacheDatabase: InMemoryCacheDatabase,
    appDatabase: AppDatabase
) : ViewModel() {

    lateinit var tvShowPagedListLiveData: LiveData<PagedList<TVShow>>

    private lateinit var requestType: RequestType
    private val trackedShowQueries = appDatabase.trackedShowQueries
    private val cachedCollectionQueries = inMemoryCacheDatabase.cachedCollectionQueries
    private val config = Config(pageSize = PAGE_SIZE, initialLoadSizeHint = PAGE_SIZE * 2)

    private val shows: DataSource.Factory<Int, TVShow>
        get() = when (requestType) {
            TRACKED -> buildTrackedDataSource()
            else -> buildCachedCollectionDataSource()
        }

    fun setRequestType(newRequestType: RequestType) {
        if (this::requestType.isInitialized && newRequestType === requestType) return
        requestType = newRequestType

        val livePagedListBuilder = LivePagedListBuilder(shows, config)

        if (newRequestType !== TRACKED) {
            val boundaryCallback =
                TVShowBoundaryCallback(
                    tmDbService,
                    viewModelScope,
                    inMemoryCacheDatabase,
                    newRequestType
                )

            livePagedListBuilder.setBoundaryCallback(boundaryCallback)
        }

        tvShowPagedListLiveData = livePagedListBuilder.build()
    }

    private fun buildTrackedDataSource(): DataSource.Factory<Int, TVShow> {
        return QueryDataSourceFactory(
            queryProvider = { limit, offset ->
                trackedShowQueries.trackedShowPaged(limit, offset, ::TVShow)
            },
            countQuery = trackedShowQueries.countTrackedShows()
        )
    }

    private fun buildCachedCollectionDataSource(): DataSource.Factory<Int, TVShow> {
        return QueryDataSourceFactory(
            queryProvider = { limit, offset ->
                cachedCollectionQueries.cachedShowPaged(requestType.name, limit, offset, ::TVShow)
            },
            countQuery = cachedCollectionQueries.countCachedShowPaged(requestType.name)
        )
    }
}

private const val PAGE_SIZE = 20
