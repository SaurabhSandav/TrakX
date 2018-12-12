package com.redridgeapps.trakx.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.redridgeapps.trakx.model.local.TrackedShow
import com.redridgeapps.trakx.model.tmdb.TVShow

@Dao
abstract class TrackedShowDao : BaseDao<TrackedShow> {

    @Query(
        """
        SELECT TVShow.* FROM TVShow
        INNER JOIN TrackedShow ON TrackedShow.showId = TVShow.id
        ORDER BY TrackedShow.id ASC
        """
    )
    abstract fun getShowsDataSource(): DataSource.Factory<Int, TVShow>

    @Query("SELECT * FROM TrackedShow")
    abstract suspend fun trackedShows(): List<TrackedShow>

    @Query("SELECT * FROM TrackedShow WHERE showId=:showId LIMIT 1")
    abstract fun getShow(showId: Int): LiveData<List<TrackedShow>>

    @Query(
        """
        SELECT TVShow.* FROM TVShow
        INNER JOIN TrackedShow ON TrackedShow.showId = TVShow.id
        WHERE TrackedShow.showId=:showId LIMIT 1
        """
    )
    abstract suspend fun getShowSingle(showId: Int): TVShow

    @Query("DELETE FROM TrackedShow WHERE showId = :showId")
    abstract fun deleteOfShowID(showId: Int)
}
