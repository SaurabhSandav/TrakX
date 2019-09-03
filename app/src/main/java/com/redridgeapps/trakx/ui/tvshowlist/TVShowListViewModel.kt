package com.redridgeapps.trakx.ui.tvshowlist

import androidx.lifecycle.SavedStateHandle
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
import com.redridgeapps.trakx.ui.common.SwapSourceLiveData
import com.redridgeapps.trakx.ui.common.dagger.AssistedViewModelFactory
import com.redridgeapps.trakx.utils.Constants.RequestType
import com.redridgeapps.trakx.utils.Constants.RequestType.TRACKED
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory

class TVShowListViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val tmDbService: TMDbService,
    private val inMemoryCacheDatabase: InMemoryCacheDatabase,
    appDatabase: AppDatabase
) : ViewModel() {

    val tvShowPagedListLiveData = SwapSourceLiveData<PagedList<TVShow>>()

    private val trackedShowQueries = appDatabase.trackedShowQueries
    private val cachedCollectionQueries = inMemoryCacheDatabase.cachedCollectionQueries
    private val config = Config(pageSize = PAGE_SIZE, initialLoadSizeHint = PAGE_SIZE * 2)

    fun setRequestType(requestType: RequestType) {

        val dataSourceFactory = when (requestType) {
            TRACKED -> buildTrackedDataSource()
            else -> buildCachedCollectionDataSource(requestType)
        }

        val livePagedListBuilder = LivePagedListBuilder(dataSourceFactory, config)

        if (requestType !== TRACKED) {
            val boundaryCallback =
                TVShowBoundaryCallback(
                    tmDbService,
                    viewModelScope,
                    inMemoryCacheDatabase,
                    requestType
                )

            livePagedListBuilder.setBoundaryCallback(boundaryCallback)
        }

        tvShowPagedListLiveData.swapSource(livePagedListBuilder.build())
    }

    private fun buildTrackedDataSource(): DataSource.Factory<Int, TVShow> = QueryDataSourceFactory(
        queryProvider = { limit, offset ->
            trackedShowQueries.trackedShowPaged(limit, offset, ::TVShow)
        },
        countQuery = trackedShowQueries.countTrackedShows()
    )

    private fun buildCachedCollectionDataSource(
        requestType: RequestType
    ): DataSource.Factory<Int, TVShow> = QueryDataSourceFactory(
        queryProvider = { limit, offset ->
            cachedCollectionQueries.cachedShowPaged(requestType.name, limit, offset, ::TVShow)
        },
        countQuery = cachedCollectionQueries.countCachedShowPaged(requestType.name)
    )

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory
}

private const val PAGE_SIZE = 20
