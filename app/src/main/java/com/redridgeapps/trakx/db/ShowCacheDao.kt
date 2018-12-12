package com.redridgeapps.trakx.db

import androidx.room.Dao
import com.redridgeapps.trakx.model.tmdb.TVShow

@Dao
abstract class ShowCacheDao : BaseDao<TVShow>
