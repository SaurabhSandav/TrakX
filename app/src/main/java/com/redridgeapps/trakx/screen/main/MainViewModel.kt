package com.redridgeapps.trakx.screen.main

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.data.TVShowBoundaryCallback
import com.redridgeapps.trakx.db.AppDatabase
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.screen.base.BaseViewModel
import com.redridgeapps.trakx.utils.Constants.RequestType
import com.redridgeapps.trakx.utils.Constants.RequestType.TRACKED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val tmDbService: TMDbService,
    private val appDatabase: AppDatabase
) : BaseViewModel() {

    lateinit var tvShowPagedListLiveData: LiveData<PagedList<TVShow>>

    private lateinit var requestType: RequestType
    private val config = Config(pageSize = PAGE_SIZE, initialLoadSizeHint = PAGE_SIZE * 2)

    private val shows: DataSource.Factory<Int, TVShow>
        get() = when (requestType) {
            TRACKED -> appDatabase.trackedShowDao().getShowsDataSource()
            else -> appDatabase.cachedCategoryDao().getShowsDataSource(requestType.name)
        }

    init {
        clearCache()
    }

    fun setRequestType(newRequestType: RequestType) {
        if (this::requestType.isInitialized && newRequestType === requestType) return
        requestType = newRequestType

        val livePagedListBuilder = LivePagedListBuilder(shows, config)

        if (newRequestType !== TRACKED) {
            val boundaryCallback = TVShowBoundaryCallback(tmDbService, appDatabase, coroutineContext, newRequestType)

            livePagedListBuilder.setBoundaryCallback(boundaryCallback)
        }

        tvShowPagedListLiveData = livePagedListBuilder.build()
    }

    private fun clearCache() = launch {
        withContext(Dispatchers.IO) { appDatabase.cachedCategoryDao().deleteAll() }
    }
}

private const val PAGE_SIZE = 20
