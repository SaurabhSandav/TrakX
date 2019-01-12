package com.redridgeapps.trakx.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.redridgeapps.trakx.model.local.CachedCategory
import com.redridgeapps.trakx.model.local.TrackedShow
import com.redridgeapps.trakx.model.tmdb.TVShow

@Database(
    entities = [
        TVShow::class,
        TrackedShow::class,
        CachedCategory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackedShowDao(): TrackedShowDao

    abstract fun showCacheDao(): ShowCacheDao

    abstract fun cachedCategoryDao(): CachedCategoryDao

    companion object {

        const val NAME = "tv_show_tracker"
    }
}
