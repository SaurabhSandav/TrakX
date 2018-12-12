package com.redridgeapps.trakx.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.redridgeapps.trakx.model.tmdb.Episode
import com.redridgeapps.trakx.model.tmdb.TVShow

@Dao
abstract class UpcomingEpisodeDao : BaseDao<Episode> {

    @Query("SELECT * FROM UpcomingEpisode ORDER BY airDate ASC")
    abstract fun upcomingEpisodes(): List<Episode>

    @Query("SELECT * FROM UpcomingEpisode WHERE showId = :showId")
    abstract fun observeOfShowID(showId: Int): LiveData<List<Episode>>

    @Query("DELETE FROM UpcomingEpisode WHERE showId = :showId")
    abstract fun deleteOfShowID(showId: Int)

    @Query("DELETE FROM UpcomingEpisode")
    abstract fun deleteAll()

    @Transaction
    open fun deleteAndInsertOfShow(tvShow: TVShow, episodesList: List<Episode>) {
        deleteOfShowID(tvShow.id)
        insertList(episodesList)
    }

    @Transaction
    open fun deleteAllAndInsertOfShow(episodesList: List<Episode>) {
        deleteAll()
        insertList(episodesList)
    }
}
