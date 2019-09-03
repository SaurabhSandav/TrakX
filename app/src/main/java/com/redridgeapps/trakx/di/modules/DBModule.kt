package com.redridgeapps.trakx.di.modules

import android.app.Application
import com.redridgeapps.trakx.AppDB
import com.redridgeapps.trakx.CacheDB
import com.redridgeapps.trakx.db.SqLiteDriverCallback
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DBModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideCacheDB(app: Application): CacheDB {

        val schema = CacheDB.Schema
        val callback = SqLiteDriverCallback(schema)
        val driver = AndroidSqliteDriver(schema, app, callback = callback)

        return CacheDB(driver)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideAppDB(app: Application): AppDB {

        val schema = AppDB.Schema
        val callback = SqLiteDriverCallback(schema)
        val driver = AndroidSqliteDriver(schema, app, "AppDB.db", callback = callback)

        return AppDB(driver)
    }
}
