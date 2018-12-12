package com.redridgeapps.trakx.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.redridgeapps.trakx.model.local.CachedCategory
import com.redridgeapps.trakx.model.tmdb.TVShow

@Dao
abstract class CachedCategoryDao : BaseDao<CachedCategory> {

    @Query(
        """
        SELECT TVShow.* FROM TVShow
        INNER JOIN CachedCategory ON CachedCategory.showId = TVShow.id
        WHERE CachedCategory.cacheCategory = :category
        ORDER BY CachedCategory.position ASC
        """
    )
    abstract fun getShowsDataSource(category: String): DataSource.Factory<Int, TVShow>

    @Query("SELECT page FROM CachedCategory WHERE cacheCategory = :category ORDER BY page DESC LIMIT 1")
    abstract suspend fun getLastPage(category: String): Int?

    @Query("DELETE FROM CachedCategory")
    abstract fun deleteAll()

}