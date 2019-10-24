package com.redridgeapps.trakx.di.modules

import android.app.Application
import com.redridgeapps.trakx.AppDB
import com.redridgeapps.trakx.CacheDB
import com.redridgeapps.trakx.CachedCollectionQueries
import com.redridgeapps.trakx.CachedShowQueries
import com.redridgeapps.trakx.TrackedShowQueries
import com.redridgeapps.trakx.UpcomingEpisodesQueries
import com.redridgeapps.trakx.db.SqLiteDriverCallback
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DBModule {

    // region CacheDB

    @Provides
    @Singleton
    fun provideCacheDB(app: Application): CacheDB {

        val schema = CacheDB.Schema
        val callback = SqLiteDriverCallback(schema)
        val driver = AndroidSqliteDriver(schema, app, callback = callback)

        return CacheDB(driver)
    }

    @Provides
    @Singleton
    fun provideCachedCollectionQueries(cacheDB: CacheDB): CachedCollectionQueries {
        return cacheDB.cachedCollectionQueries
    }

    @Provides
    @Singleton
    fun provideCachedShowQueries(cacheDB: CacheDB): CachedShowQueries {
        return cacheDB.cachedShowQueries
    }

    // endregion CacheDB

    // region AppDB

    @Provides
    @Singleton
    fun provideAppDB(app: Application): AppDB {

        val schema = AppDB.Schema
        val callback = SqLiteDriverCallback(schema)
        val driver = AndroidSqliteDriver(schema, app, "AppDB.db", callback = callback)

        return AppDB(driver)
    }

    @Provides
    @Singleton
    fun provideTrackedShowQueries(appDB: AppDB): TrackedShowQueries {
        return appDB.trackedShowQueries
    }

    @Provides
    @Singleton
    fun provideUpcomingEpisodesQueries(appDB: AppDB): UpcomingEpisodesQueries {
        return appDB.upcomingEpisodesQueries
    }

    // endregion AppDB
}
